# Instructions for candidates

This is the Java version of the Payment Gateway challenge. If you haven't already read this
[README.md](https://github.com/cko-recruitment/) on the details of this exercise, please do so now.

---

## Requirements

- JDK 17
- Docker

---

## Template Structure

| Path | Description |
|---|---|
| `src/` | A skeleton SpringBoot Application |
| `test/` | Some simple JUnit tests |
| `imposters/` | Contains the bank simulator configuration. Don't change this |
| `.editorconfig` | Don't change this. It ensures a consistent set of rules for submissions when reformatting code |
| `docker-compose.yml` | Configures the bank simulator |

---

## API Documentation

OpenAPI is included for documentation and can be found at:
**http://localhost:8090/swagger-ui/index.html**

> Feel free to change the structure of the solution, use a different library etc.

---

## Testing

This repository includes a mix of unit and integration tests that cover the core payment workflow.
The tests cover:

- Processing payments through **authorised**, **declined**, and **rejected** paths
- Enforcing validation rules on the request
- Mapping internal and external models
- Handling exceptions

| Test Type | Coverage |
|---|---|
| **Integration** | HTTP API layer tested using MockMvc |
| **Unit** | Service layer, validators, mappers, exception handlers, and acquiring bank client behaviour |

---

## Design Considerations and Assumptions

Below is a summary of decisions and assumptions made while completing this challenge.
The goal was to address the main requirements while keeping the solution simple, open for extension,
and well tested.

---

### Controller

The controller was partially implemented. A second REST API endpoint was added to meet the
requirements of the challenge.

A `PaymentService` interface was used to connect the service and controller, following the
**dependency inversion principle**, in order to:

- Improve testability via mocking
- Decouple the controller and service layers
- Provide a clear contract definition

---

### Model

The model was also partially implemented. Below is a breakdown of the decisions made.

#### `PostPaymentRequest`

Changes were made to the existing implementation:

- **Jakarta validations and REGEX** were used to enforce validation requirements because:
  - *Validation at the gate:* requests are validated before entering any service layer, as required
  - *Consistency:* previous experience with Jakarta allowed the addition of `ExpiryDateValidator`
    to keep validation logic in one place
- **Type changes:** `int` was replaced for some fields because `int` removes leading zeros in Java.
  This is an assumption, as no requirements defined field types. This could break the vendor
  contract and an alternative solution may be needed.
- **`toString()` method:** CVV was previously printed in a human-readable format, this was removed
  for security.

#### `GetPaymentResponse` and `PostPaymentResponse`

These were also partially implemented. Although both share the same structure, merging them was
considered and rejected for two reasons:

- **Separation of concerns:** The GET and POST responses may need to return different data in the
  future, so building with extension in mind was preferred.
- **Existing implementation:** Two separate responses were already present, suggesting this was the
  direction preferred by the code owners.

Both responses were converted to **records**, which:

- Eliminates boilerplate code
- Makes responses immutable by default
- Simplifies readability and understanding

---

### Service

This layer was also partially implemented. **Constructor-based dependency injection** was used to
ensure:

- Immutability
- Thread safety
- Ease of unit testing
- Compliance with SOLID principles

The layer is kept clear of business logic, with interfaces separating it from the client and
repository code. Mappers are used to avoid adding builders or constructors to the service.

> **Assumption on ID:** The `authorization_code` from the downstream response was used as the
> payment ID, as it appears to be a UUID. However, this may not align with the requirement that
> states *"Feel free to choose whatever format you think makes most sense."* In a real scenario,
> this would be raised in standup or refinement before proceeding.

---

### Repository

The repository is largely unchanged. The main changes were:

- `PostPaymentResponse` was replaced with `PaymentEntity` as a result of having two separate
  response types (`GetPaymentResponse` and `PostPaymentResponse`)
- A `PaymentStore` interface was added to decouple the repository from the service layer, making
  it easier to swap the implementation in the future

---

### Client

This handles all interaction with the downstream acquiring bank API. It is kept decoupled from the
service layer and is solely responsible for sending requests downstream.

Error handling assumptions were made where existing standards were unavailable:

- **HTTP error responses** (4xx/5xx) are handled by `BankResponseErrorHandler`, which logs the
  status code and response body, then throws a `BankProcessingException`
- **Network/connection errors** are caught in the client's catch block, which logs the exception
  message and also throws a `BankProcessingException`
- **Merchant response:** The merchant always receives a generic `500 Internal Server Error` to
  avoid exposing internal connection details. Existing service standards would be followed in a
  real scenario.

A `BankRequest` and `BankResponse` record are also included. Data-specific logic is encapsulated
within `BankResponse` to keep sanitisation and translation concerns in one place.

> An `AcquiringBankClient` interface is used, consistent with the pattern applied across the rest
> of the codebase, to decouple the client from the service layer.

---

### Exception Handling

The existing `CommonExceptionHandler` was extended to handle the following exceptions:

| Exception | Trigger | HTTP Status |
|---|---|---|
| `MethodArgumentNotValidException` | Any violation of request validation rules | `400 Bad Request` |
| `BankProcessingException` | Any issue connecting to the downstream service | `500 Internal Server Error` |
| `EventProcessingException` | Record not found (previously a page not found handler) | `404 Not Found` |

> **Note:** `EventProcessingException` should ideally be renamed to something like
> `NotFoundException` to better reflect its purpose. The response message was also updated from
> *"page not found"* to *"Record not found"* as this was considered more appropriate.

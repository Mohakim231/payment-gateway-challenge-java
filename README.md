# Instructions for candidates

This is the Java version of the Payment Gateway challenge. If you haven't already read this [README.md](https://github.com/cko-recruitment/) on the details of this exercise, please do so now.

## Requirements
- JDK 17
- Docker

## Template structure

src/ - A skeleton SpringBoot Application

test/ - Some simple JUnit tests

imposters/ - contains the bank simulator configuration. Don't change this

.editorconfig - don't change this. It ensures a consistent set of rules for submissions when reformatting code

docker-compose.yml - configures the bank simulator


## API Documentation
For documentation openAPI is included, and it can be found under the following url: **http://localhost:8090/swagger-ui/index.html**

**Feel free to change the structure of the solution, use a different library etc.**

## Documentation

## Testing
This repository includes a mix of unit and integration tests that cover the ore payment workflow. The tests cover processing the payments through authorised, declined and rejected paths, enforcing validation rules on the request, mapping internal and external models, and handling exceptions.
Integration tests test the HTTP API using MockMvc and unit tests target the service layer, validators, mappers, exception handlers, and acquiring bank client behavior.

## Design considerations and assumptions
I will give a summary over my decisions and assumptions while attempting this challenge:

I wanted to address the main asks for the challenge while keeping the solution simple open for extension and well testsed:

Controller:
The controller was partially implemented I added a second REST API endpoint to meet the requirements of the challenge. I used an interface `PaymentService` to connect my service and my controller following the dependency inversion principle - to help with testing (Mocking), decouple the controller and service layer and to have clear contract definition.

Model:
The model was also partially implemented I will start by breaking down decisions made in -
PostPaymentRequest:
Changes were made to the already implemented version of the request jakarta validations and REGEX codes were used to cover the validation requirements from the request I chose this implementation because:
- Validation at the gate: wanted to validate requests before we enter any services as this was a requirement.
- Previous experience with jakarta and the ability to add `ExpiryDateValidator` to keep the logic consistent.

There was also changes in types int was used for some values but because int removes leading 0s in java I chose to replace this implementation for simplicity this is an assumption I made as there was no requirements that outline what the type for fields should be. This could break the contract with the vendor so a different solution could be required.
The toString method was also printing out cvv in a human-readable format, so I removed this for safety.

GetPaymentResponse and PostPaymentResponse:
These were also partially implemented - since they have the same structure and body I considered merging both, however I decided against that for two reasons:
- Separation of concerns: The get and post response may want to return different things in the future so wanted to build with that extension potential in mind.
- Existing implementation: There was already two separate responses so I assumed this was direction preferred by the code owners.

I also changed the structure of these responses to use records. This eliminates a lot of the boilerplate code it also eliminates the need for mappers makes the responses immutable by default and makes them simpler to understand.

Service:
This layer was also partially implemented. I started by using dependency injection to ensure:
- Immutability
- Thread safety
- Ease of unit testing
- Compliance with SOLID principles
I kept the layer clear of business logic and used interfaces to separate it from client and repository code. I also used mappers to avoid adding builders or constructors to the service.


Repository:
The repository is largely unchanged I replaced the previous PostPaymentResponse with a PaymentEntitiy. This is a result of using two different responses the GetPaymentResponse and PostPaymentResponse. I also added an interface to decouple it from the service layer this would make it easier to replace the existing repository with a different solution in the future.

Client:
This was our interaction with the down stream API. I kept this decoupled from the service and built it to only send the requests to downstream. I made assumptions about error handling where I would usually clarify with product and follow existing standards used by other services.
- In case of an error in connecting to the api. I added `log.error` that prints out the status and the body fir debugging purposes. The Merchant will always receive an internal server error. This is because I assumed we don't want the merchant being aware of our underlying connections and issues but I would look for guidance on existing practices here.

This is handled by the `BankResponseErrorHandler` while connection errors are handled by the catch block in the client. Similar to other classes this uses an interface to decouple it from the service layer.

There is also a bank request and response record I also encapsulated data specific logic within the response record the purpose of those methods is to sanitise and translate the data

Exception handling:
I extended upon the existing CommonExceptionHandler implementation. I now throw:
- MethodArgumentNotValidException: this is for any violation of the validation rules.
- BankProcessingException: For any issue connecting to the downstream service.
- EventProcessingException: This was the existing exception which should change to some sort of NotFoundException. I did change the error message from page not found to Record not found as I assumed more appropriate.





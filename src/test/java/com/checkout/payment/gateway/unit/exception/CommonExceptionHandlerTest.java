package com.checkout.payment.gateway.unit.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.BankProcessingException;
import com.checkout.payment.gateway.exception.CommonExceptionHandler;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.response.ErrorResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class CommonExceptionHandlerTest {

  private CommonExceptionHandler exceptionHandler;

  @BeforeEach
  void setUp() {
    exceptionHandler = new CommonExceptionHandler();
  }

  @Test
  void handleEventProcessingException_Returns404() {
    EventProcessingException ex = new EventProcessingException("Not found");
    ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(ex);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Record not found", response.getBody().getMessage());
  }

  @Test
  void handleBankProcessingException_Returns500() {
    String genericMessage = "Your payment could not be processed at this time. Please try again later.";
    BankProcessingException ex = new BankProcessingException(genericMessage);

    ResponseEntity<ErrorResponse> response = exceptionHandler.handleBankProcessingException(ex);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(genericMessage, response.getBody().getMessage());
  }

  @Test
  void handleValidationException_ReturnsRejectedStatusAndErrors() {
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError = new FieldError("postPaymentRequest", "currency", "Currency must be USD, EUR, or GBP");
    when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

    MethodParameter methodParameter = mock(MethodParameter.class);
    MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

    ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationException(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(PaymentStatus.REJECTED, response.getBody().get("status"));

    List<String> errors = (List<String>) response.getBody().get("errors");
    assertTrue(errors.get(0).contains("currency: Currency must be USD"));
  }
}

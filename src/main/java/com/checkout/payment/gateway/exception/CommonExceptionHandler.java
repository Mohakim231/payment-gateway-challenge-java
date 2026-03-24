package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.ErrorResponse;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class CommonExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

  @ExceptionHandler(EventProcessingException.class)
  public ResponseEntity<ErrorResponse> handleException(EventProcessingException ex) {
    LOG.error("Exception happened", ex);
    return new ResponseEntity<>(new ErrorResponse("Record not found"),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
    LOG.warn("Payment request validation failed.");

    Map<String, Object> response = new HashMap<>();
    response.put("status", PaymentStatus.REJECTED);

    List<String> errors = ex.getBindingResult()
        .getAllErrors()
        .stream()
        .map(error -> {
          if (error instanceof org.springframework.validation.FieldError) {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            return fieldName + ": " + error.getDefaultMessage();
          }
          return error.getDefaultMessage();
        })
        .collect(Collectors.toList());

    response.put("errors", errors);

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }


  @ExceptionHandler(BankProcessingException.class)
  public ResponseEntity<ErrorResponse> handleBankProcessingException(BankProcessingException ex) {
    return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

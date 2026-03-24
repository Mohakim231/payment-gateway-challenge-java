package com.checkout.payment.gateway.unit.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.checkout.payment.gateway.model.request.PostPaymentRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.YearMonth;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PostPaymentRequestValidationTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void testBoundary_ValidCardLengths() {
    PostPaymentRequest request14 = createValidRequest();
    request14.setCardNumber("12345678901234");
    assertTrue(validator.validate(request14).isEmpty());

    PostPaymentRequest request19 = createValidRequest();
    request19.setCardNumber("1234567890123456789");
    assertTrue(validator.validate(request19).isEmpty());
  }

  @Test
  void testBoundary_ValidCvvLengths() {
    PostPaymentRequest request4 = createValidRequest();
    request4.setCvv("1234");
    assertTrue(validator.validate(request4).isEmpty());
  }

  @Test
  void testMissingRequiredFields() {
    PostPaymentRequest request = new PostPaymentRequest();
    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(request);

    assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Card number is required")));
    assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Expiry month is required")));
    assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Expiry year is required")));
    assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Currency is required")));
    assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("CVV is required")));
  }

  private PostPaymentRequest createValidRequest() {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("1234567890123456");
    request.setExpiryMonth(12);
    request.setExpiryYear(YearMonth.now().getYear() + 1);
    request.setCurrency("USD");
    request.setAmount(1000L);
    request.setCvv("123");
    return request;
  }
}

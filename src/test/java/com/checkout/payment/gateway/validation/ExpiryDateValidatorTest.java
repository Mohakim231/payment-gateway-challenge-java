package com.checkout.payment.gateway.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import java.time.YearMonth;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ExpiryDateValidatorTest {

  private ExpiryDateValidator validator;

  @BeforeEach
  void setUp() {
    validator = new ExpiryDateValidator();
  }

  @ParameterizedTest(name = "Expiry: {0}/{1} -> Expected Valid: {2}")
  @MethodSource("provideDatesForValidation")
  void testIsValid(Integer year, Integer month, boolean expectedResult) {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setExpiryYear(year);
    request.setExpiryMonth(month);

    boolean result = validator.isValid(request, null);

    assertEquals(expectedResult, result);
  }

  static Stream<Arguments> provideDatesForValidation() {
    YearMonth now = YearMonth.now();
    return Stream.of(
        Arguments.of(now.getYear() + 1, now.getMonthValue(), true),
        Arguments.of(now.getYear() + 5, 12, true),
        Arguments.of(now.getYear(), now.getMonthValue(), false),
        Arguments.of(now.getYear() - 1, now.getMonthValue(), false),
        Arguments.of(2020, 1, false)
    );
  }
}

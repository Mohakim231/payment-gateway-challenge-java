package com.checkout.payment.gateway.validation;

import com.checkout.payment.gateway.model.request.PostPaymentRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.YearMonth;

public class ExpiryDateValidator implements
    ConstraintValidator<ValidExpiryDate, PostPaymentRequest> {
  @Override
  public boolean isValid(PostPaymentRequest request, ConstraintValidatorContext context) {
    if (request == null) {
      return true;
    }

    try {
      YearMonth currentMonth = YearMonth.now();
      YearMonth cardExpiry = YearMonth.of(request.getExpiryYear(), request.getExpiryMonth());

      return cardExpiry.isAfter(currentMonth);

    } catch (Exception e) {
      return false;
    }
  }
}

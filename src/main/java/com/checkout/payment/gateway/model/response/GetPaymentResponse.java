package com.checkout.payment.gateway.model.response;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.repository.entity.PaymentEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record GetPaymentResponse(
    UUID id,
    PaymentStatus status,
    @JsonProperty("last_four_card_digits") String cardNumberLastFour,
    @JsonProperty("expiry_month") Integer expiryMonth,
    @JsonProperty("expiry_year") Integer expiryYear,
    String currency,
    Long amount
) {
  public static GetPaymentResponse from(PaymentEntity entity) {
    return new GetPaymentResponse(
        entity.getId(),
        entity.getStatus(),
        entity.getCardNumberLastFour(),
        entity.getExpiryMonth(),
        entity.getExpiryYear(),
        entity.getCurrency(),
        entity.getAmount()
    );
  }
}
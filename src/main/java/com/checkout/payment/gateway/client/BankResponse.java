package com.checkout.payment.gateway.client;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

public record BankResponse(
    boolean authorized,
    @JsonProperty("authorization_code") String authorizationCode
) {
  private static final Logger LOG = LoggerFactory.getLogger(BankResponse.class);

  public BankResponse {
    authorizationCode = (authorizationCode != null) ? authorizationCode.trim() : "";
  }

  public UUID toPaymentId() {
    if (authorized && !authorizationCode.isEmpty()) {
      try {
        return UUID.fromString(authorizationCode);
      } catch (IllegalArgumentException e) {
        LOG.warn("Invalid authorization code format: {}, generating random ID", authorizationCode);
        return UUID.randomUUID();
      }
    }
    return UUID.randomUUID();
  }

  public PaymentStatus toPaymentStatus() {
    return authorized ? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED;
  }
}

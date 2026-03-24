package com.checkout.payment.gateway.repository.entity;

import com.checkout.payment.gateway.enums.PaymentStatus;
import java.util.UUID;

public class PaymentEntity {
  private UUID id;
  private PaymentStatus status;
  private String cardNumberLastFour;
  private Integer expiryMonth;
  private Integer expiryYear;
  private String currency;
  private Long amount;

  protected PaymentEntity() {}

  public PaymentEntity(UUID id, PaymentStatus status, String cardNumberLastFour,
      Integer expiryMonth, Integer expiryYear,
      String currency, Long amount) {
    this.id = id;
    this.status = status;
    this.cardNumberLastFour = cardNumberLastFour;
    this.expiryMonth = expiryMonth;
    this.expiryYear = expiryYear;
    this.currency = currency;
    this.amount = amount;
  }

  public UUID getId() { return id; }
  public PaymentStatus getStatus() { return status; }
  public String getCardNumberLastFour() { return cardNumberLastFour; }
  public Integer getExpiryMonth() { return expiryMonth; }
  public Integer getExpiryYear() { return expiryYear; }
  public String getCurrency() { return currency; }
  public Long getAmount() { return amount; }
}
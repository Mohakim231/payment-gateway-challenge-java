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

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public PaymentStatus getStatus() { return status; }
  public void setStatus(PaymentStatus status) { this.status = status; }
  public String getCardNumberLastFour() { return cardNumberLastFour; }
  public void setCardNumberLastFour(String cardNumberLastFour) { this.cardNumberLastFour = cardNumberLastFour; }
  public Integer getExpiryMonth() { return expiryMonth; }
  public void setExpiryMonth(Integer expiryMonth) { this.expiryMonth = expiryMonth; }
  public Integer getExpiryYear() { return expiryYear; }
  public void setExpiryYear(Integer expiryYear) { this.expiryYear = expiryYear; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
  public Long getAmount() { return amount; }
  public void setAmount(Long amount) { this.amount = amount; }
}

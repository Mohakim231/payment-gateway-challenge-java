package com.checkout.payment.gateway.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BankRequest {
  @JsonProperty("card_number")
  private String card_number;

  @JsonProperty("expiry_date")
  private String expiry_date;

  private String currency;
  private Long amount;
  private String cvv;

  public String getCardNumber() { return card_number; }
  public void setCardNumber(String cardNumber) { this.card_number = cardNumber; }

  public String getExpiryDate() { return expiry_date; }
  public void setExpiryDate(String expiryDate) { this.expiry_date = expiryDate; }

  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }

  public Long getAmount() { return amount; }
  public void setAmount(Long amount) { this.amount = amount; }

  public String getCvv() { return cvv; }
  public void setCvv(String cvv) { this.cvv = cvv; }
}

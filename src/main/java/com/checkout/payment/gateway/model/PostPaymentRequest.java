package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.validation.ValidExpiryDate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;

//can be a record java 17 - removes boiler plate makes it immutable does record override equals and hashcode
@ValidExpiryDate
public class PostPaymentRequest implements Serializable {

  @NotBlank(message = "Card number is required")
  @Pattern(regexp = "^[0-9]{14,19}$", message = "Card number must be 14-19 numeric characters")
  @JsonProperty("card_number")
  private String cardNumber;

  @NotNull(message = "Expiry month is required")
  @Min(value = 1, message = "Expiry month must be at least 1")
  @Max(value = 12, message = "Expiry month must be at most 12")
  @JsonProperty("expiry_month")
  private Integer expiryMonth;

  @NotNull(message = "Expiry year is required")
  @JsonProperty("expiry_year")
  private Integer expiryYear;

  @NotBlank(message = "Currency is required")
  @Pattern(regexp = "^(USD|EUR|GBP)$", message = "Currency must be USD, EUR, or GBP")
  private String currency;

  @NotNull
  @Positive
  private Long amount;

  @NotBlank(message = "CVV is required")
  @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be 3-4 numeric characters")
  private String cvv;

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public Integer getExpiryMonth() {
    return expiryMonth;
  }

  public void setExpiryMonth(Integer expiryMonth) {
    this.expiryMonth = expiryMonth;
  }

  public Integer getExpiryYear() {
    return expiryYear;
  }

  public void setExpiryYear(Integer expiryYear) {
    this.expiryYear = expiryYear;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public Long getAmount() {
    return amount;
  }

  public void setAmount(Long amount) {
    this.amount = amount;
  }

  public String getCvv() {
    return cvv;
  }

  public void setCvv(String cvv) {
    this.cvv = cvv;
  }

  @JsonProperty("expiry_date")
  public String getExpiryDate() {
    return String.format("%02d/%d", expiryMonth, expiryYear);
  }

  public String getLastFourCardDigits() {
    if (this.cardNumber == null) {
      return null;
    }
    if (this.cardNumber.length() < 4) {
      return this.cardNumber;
    }
    return cardNumber.substring(cardNumber.length() - 4);
  }

  @Override
  public String toString() {
    return "PostPaymentRequest{" +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        '}';
  }
}

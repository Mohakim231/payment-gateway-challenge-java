package com.checkout.payment.gateway.constant;

public final class Constants {

  private Constants() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static final String CARD_NUMBER_REGEX = "^[0-9]{14,19}$";
  public static final String CURRENCY_REGEX = "^(USD|EUR|GBP)$";
  public static final String CVV_REGEX = "^[0-9]{3,4}$";
  public static final String ACQUIRING_BANK_PROCESS_URL = "/payments";

}

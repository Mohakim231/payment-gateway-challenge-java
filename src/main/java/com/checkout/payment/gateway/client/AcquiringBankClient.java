package com.checkout.payment.gateway.client;

public interface AcquiringBankClient {
  BankResponse processBankPayment(BankRequest bankRequest);
}

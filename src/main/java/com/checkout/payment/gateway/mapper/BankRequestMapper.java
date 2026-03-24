package com.checkout.payment.gateway.mapper;

import com.checkout.payment.gateway.client.BankRequest;
import com.checkout.payment.gateway.model.PostPaymentRequest;

public interface BankRequestMapper {
  BankRequest toBankRequest(PostPaymentRequest request);
}

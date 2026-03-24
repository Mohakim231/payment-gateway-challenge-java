package com.checkout.payment.gateway.mapper;

import com.checkout.payment.gateway.client.BankRequest;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import org.springframework.stereotype.Component;

@Component
public class BankRequestMapperImpl implements BankRequestMapper {
  @Override
  public BankRequest toBankRequest(PostPaymentRequest request) {
    BankRequest bankRequest = new BankRequest();
    bankRequest.setCardNumber(request.getCardNumber());
    bankRequest.setExpiryDate(request.getExpiryDate());
    bankRequest.setCurrency(request.getCurrency());
    bankRequest.setAmount(request.getAmount());
    bankRequest.setCvv(request.getCvv());
    return bankRequest;
  }
}

package com.checkout.payment.gateway.service.mapper;

import com.checkout.payment.gateway.client.BankRequest;
import com.checkout.payment.gateway.model.request.PostPaymentRequest;
import org.springframework.stereotype.Component;

@Component
public class BankRequestMapper {
    public BankRequest from(PostPaymentRequest request) {
        return new BankRequest(
                request.getCardNumber(),
                request.getExpiryDate(),
                request.getCurrency(),
                request.getAmount(),
                request.getCvv()
        );
    }
}

package com.checkout.payment.gateway.service.mapper;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.request.PostPaymentRequest;
import com.checkout.payment.gateway.repository.entity.PaymentEntity;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PaymentRequestMapper {
  public PaymentEntity from(PostPaymentRequest request, PaymentStatus status, UUID id) {
    return new PaymentEntity(
        id,
        status,
        request.getLastFourCardDigits(),
        request.getExpiryMonth(),
        request.getExpiryYear(),
        request.getCurrency(),
        request.getAmount()
    );
  }
}

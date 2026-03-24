package com.checkout.payment.gateway.mapper;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.repository.entity.PaymentEntity;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class PaymentRequestMapperImpl implements PaymentRequestMapper {
  @Override
  public PaymentEntity create(PostPaymentRequest request, PaymentStatus status, UUID id) {
    PaymentEntity entity = new PaymentEntity();
    entity.setId(id);
    entity.setStatus(status);
    entity.setCardNumberLastFour(request.getLastFourCardDigits());
    entity.setExpiryMonth(request.getExpiryMonth());
    entity.setExpiryYear(request.getExpiryYear());
    entity.setCurrency(request.getCurrency());
    entity.setAmount(request.getAmount());
    return entity;
  }
}

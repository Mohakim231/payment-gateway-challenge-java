package com.checkout.payment.gateway.mapper;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.repository.entity.PaymentEntity;
import java.util.UUID;

public interface PaymentRequestMapper {
  PaymentEntity create(PostPaymentRequest request, PaymentStatus status, UUID id);
}

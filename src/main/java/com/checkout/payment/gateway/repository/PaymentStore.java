package com.checkout.payment.gateway.repository;

import com.checkout.payment.gateway.repository.entity.PaymentEntity;
import java.util.Optional;
import java.util.UUID;

public interface PaymentStore {
  void add(PaymentEntity payment);
  Optional<PaymentEntity> get(UUID id);
}


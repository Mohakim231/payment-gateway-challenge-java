package com.checkout.payment.gateway.repository;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import com.checkout.payment.gateway.repository.entity.PaymentEntity;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentsRepository implements PaymentStore {
  private final HashMap<UUID, PaymentEntity> payments = new HashMap<>();

  @Override
  public void add(PaymentEntity payment) {
    payments.put(payment.getId(), payment);
  }

  @Override
  public Optional<PaymentEntity> get(UUID id) {
    return Optional.ofNullable(payments.get(id));
  }
}

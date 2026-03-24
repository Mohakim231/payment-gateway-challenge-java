package com.checkout.payment.gateway.repository;

import com.checkout.payment.gateway.model.GetPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import com.checkout.payment.gateway.repository.entity.PaymentEntity;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentsRepository {

  private final HashMap<UUID, PaymentEntity> payments = new HashMap<>();

  public void add(PaymentEntity payment) {
    payments.put(payment.getId(), payment);
  }

  public Optional<PaymentEntity> get(UUID id) {
    return Optional.ofNullable(payments.get(id));
  }

}

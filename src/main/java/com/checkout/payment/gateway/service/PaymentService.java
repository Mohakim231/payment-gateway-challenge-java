package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.model.GetPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import java.util.UUID;

public interface PaymentService {
  PostPaymentResponse processPayment(PostPaymentRequest request);
  GetPaymentResponse getPaymentById(UUID id);
}

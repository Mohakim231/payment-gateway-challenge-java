package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.model.response.GetPaymentResponse;
import com.checkout.payment.gateway.model.request.PostPaymentRequest;
import com.checkout.payment.gateway.model.response.PostPaymentResponse;
import java.util.UUID;

public interface PaymentService {
  PostPaymentResponse processPayment(PostPaymentRequest request);
  GetPaymentResponse getPaymentById(UUID id);
}

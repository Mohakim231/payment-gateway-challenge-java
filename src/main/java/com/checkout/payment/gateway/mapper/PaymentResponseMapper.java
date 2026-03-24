package com.checkout.payment.gateway.mapper;

import com.checkout.payment.gateway.model.GetPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.entity.PaymentEntity;

public interface PaymentResponseMapper {
  PostPaymentResponse toPostResponse(PaymentEntity entity);
  GetPaymentResponse toGetResponse(PaymentEntity entity);
}

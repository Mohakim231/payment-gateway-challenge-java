package com.checkout.payment.gateway.mapper;

import com.checkout.payment.gateway.model.GetPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.entity.PaymentEntity;
import org.springframework.stereotype.Component;

@Component
public class PaymentResponseMapperImpl implements PaymentResponseMapper {
  @Override
  public PostPaymentResponse toPostResponse(PaymentEntity entity) {
    PostPaymentResponse response = new PostPaymentResponse();
    response.setId(entity.getId());
    response.setStatus(entity.getStatus());
    response.setCardNumberLastFour(entity.getCardNumberLastFour());
    response.setExpiryMonth(entity.getExpiryMonth());
    response.setExpiryYear(entity.getExpiryYear());
    response.setCurrency(entity.getCurrency());
    response.setAmount(entity.getAmount());
    return response;
  }

  @Override
  public GetPaymentResponse toGetResponse(PaymentEntity entity) {
    GetPaymentResponse response = new GetPaymentResponse();
    response.setId(entity.getId());
    response.setStatus(entity.getStatus());
    response.setCardNumberLastFour(entity.getCardNumberLastFour());
    response.setExpiryMonth(entity.getExpiryMonth());
    response.setExpiryYear(entity.getExpiryYear());
    response.setCurrency(entity.getCurrency());
    response.setAmount(entity.getAmount());
    return response;
  }
}

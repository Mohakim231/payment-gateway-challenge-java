package com.checkout.payment.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.GetPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.entity.PaymentEntity;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MappersTest {

  private final PaymentRequestMapper requestMapper = new PaymentRequestMapperImpl();
  private final PaymentResponseMapper responseMapper = new PaymentResponseMapperImpl();

  @Test
  void testPaymentRequestMapper_14DigitCard() {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("12345678901234");
    request.setExpiryMonth(12);
    request.setExpiryYear(2030);
    request.setCurrency("GBP");
    request.setAmount(500L);

    UUID id = UUID.randomUUID();
    PaymentEntity entity = requestMapper.create(request, PaymentStatus.AUTHORIZED, id);

    assertEquals("1234", entity.getCardNumberLastFour());
    assertEquals(PaymentStatus.AUTHORIZED, entity.getStatus());
  }

  @Test
  void testPaymentResponseMapper_AssertAllFields() {
    PaymentEntity entity = new PaymentEntity();
    UUID id = UUID.randomUUID();
    entity.setId(id);
    entity.setStatus(PaymentStatus.DECLINED);
    entity.setCardNumberLastFour("1111");
    entity.setExpiryMonth(10);
    entity.setExpiryYear(2025);
    entity.setCurrency("EUR");
    entity.setAmount(250L);

    PostPaymentResponse postResponse = responseMapper.toPostResponse(entity);
    assertEquals(id, postResponse.getId());
    assertEquals(PaymentStatus.DECLINED, postResponse.getStatus());
    assertEquals("1111", postResponse.getCardNumberLastFour());
    assertEquals(10, postResponse.getExpiryMonth());
    assertEquals(2025, postResponse.getExpiryYear());
    assertEquals("EUR", postResponse.getCurrency());
    assertEquals(250L, postResponse.getAmount());

    GetPaymentResponse getResponse = responseMapper.toGetResponse(entity);
    assertEquals(id, getResponse.getId());
    assertEquals(PaymentStatus.DECLINED, getResponse.getStatus());
    assertEquals("1111", getResponse.getCardNumberLastFour());
    assertEquals(10, getResponse.getExpiryMonth());
    assertEquals(2025, getResponse.getExpiryYear());
    assertEquals("EUR", getResponse.getCurrency());
    assertEquals(250L, getResponse.getAmount());
  }
}

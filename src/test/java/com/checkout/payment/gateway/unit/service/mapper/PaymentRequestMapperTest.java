package com.checkout.payment.gateway.unit.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.request.PostPaymentRequest;
import com.checkout.payment.gateway.model.response.GetPaymentResponse;
import com.checkout.payment.gateway.model.response.PostPaymentResponse;
import com.checkout.payment.gateway.repository.entity.PaymentEntity;
import java.util.UUID;
import com.checkout.payment.gateway.service.mapper.PaymentRequestMapper;
import org.junit.jupiter.api.Test;

class PaymentRequestMapperTest {

  private final PaymentRequestMapper paymentRequestMapper = new PaymentRequestMapper();

  @Test
  void from_mapsLastFourDigitsCorrectly() {
    PostPaymentRequest request = buildPostPaymentRequest("12345678901234");

    PaymentEntity entity = paymentRequestMapper.from(request, PaymentStatus.AUTHORIZED, UUID.randomUUID());

    assertEquals("1234", entity.getCardNumberLastFour());
  }

  @Test
  void from_mapsStatusCorrectly() {
    PostPaymentRequest request = buildPostPaymentRequest("12345678901234");

    PaymentEntity entity = paymentRequestMapper.from(request, PaymentStatus.AUTHORIZED, UUID.randomUUID());

    assertEquals(PaymentStatus.AUTHORIZED, entity.getStatus());
  }

  @Test
  void from_mapsIdCorrectly() {
    PostPaymentRequest request = buildPostPaymentRequest("12345678901234");
    UUID id = UUID.randomUUID();

    PaymentEntity entity = paymentRequestMapper.from(request, PaymentStatus.AUTHORIZED, id);

    assertEquals(id, entity.getId());
  }

  @Test
  void from_mapsAllFieldsCorrectly() {
    PostPaymentRequest request = buildPostPaymentRequest("12345678901234");
    UUID id = UUID.randomUUID();

    PaymentEntity entity = paymentRequestMapper.from(request, PaymentStatus.AUTHORIZED, id);

    assertEquals(id, entity.getId());
    assertEquals(PaymentStatus.AUTHORIZED, entity.getStatus());
    assertEquals("1234", entity.getCardNumberLastFour());
    assertEquals(12, entity.getExpiryMonth());
    assertEquals(2030, entity.getExpiryYear());
    assertEquals("GBP", entity.getCurrency());
    assertEquals(500L, entity.getAmount());
  }

  @Test
  void getPaymentResponse_mapsAllFieldsCorrectly() {
    PaymentEntity entity = buildPaymentEntity();

    GetPaymentResponse response = GetPaymentResponse.from(entity);

    assertEquals(entity.getId(), response.id());
    assertEquals(PaymentStatus.DECLINED, response.status());
    assertEquals("1111", response.cardNumberLastFour());
    assertEquals(10, response.expiryMonth());
    assertEquals(2025, response.expiryYear());
    assertEquals("EUR", response.currency());
    assertEquals(250L, response.amount());
  }

  @Test
  void postPaymentResponse_mapsAllFieldsCorrectly() {
    PaymentEntity entity = buildPaymentEntity();

    PostPaymentResponse response = PostPaymentResponse.from(entity);

    assertEquals(entity.getId(), response.id());
    assertEquals(PaymentStatus.DECLINED, response.status());
    assertEquals("1111", response.cardNumberLastFour());
    assertEquals(10, response.expiryMonth());
    assertEquals(2025, response.expiryYear());
    assertEquals("EUR", response.currency());
    assertEquals(250L, response.amount());
  }

  private PostPaymentRequest buildPostPaymentRequest(String cardNumber) {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber(cardNumber);
    request.setExpiryMonth(12);
    request.setExpiryYear(2030);
    request.setCurrency("GBP");
    request.setAmount(500L);
    request.setCvv("123");
    return request;
  }

  private PaymentEntity buildPaymentEntity() {
    return new PaymentEntity(
        UUID.randomUUID(),
        PaymentStatus.DECLINED,
        "1111",
        10,
        2025,
        "EUR",
        250L
    );
  }
}

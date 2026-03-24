package com.checkout.payment.gateway.unit.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.checkout.payment.gateway.client.BankRequest;
import com.checkout.payment.gateway.client.BankResponse;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.request.PostPaymentRequest;
import java.util.UUID;
import com.checkout.payment.gateway.service.mapper.BankRequestMapper;
import org.junit.jupiter.api.Test;

class BankRequestMapperTest {

  private final BankRequestMapper bankRequestMapper = new BankRequestMapper();

  @Test
  void from_mapsAllFieldsCorrectly() {
    PostPaymentRequest request = buildPostPaymentRequest("12345678901234");

    BankRequest bankRequest = bankRequestMapper.from(request);

    assertNotNull(bankRequest);
    assertEquals("12345678901234", bankRequest.cardNumber());
    assertEquals("12/2030", bankRequest.expiryDate());
    assertEquals("GBP", bankRequest.currency());
    assertEquals(500L, bankRequest.amount());
    assertEquals("123", bankRequest.cvv());
  }

  @Test
  void bankResponse_whenAuthorized_returnsAuthorizedStatus() {
    BankResponse response = new BankResponse(true, UUID.randomUUID().toString());

    assertEquals(PaymentStatus.AUTHORIZED, response.toPaymentStatus());
  }

  @Test
  void bankResponse_whenNotAuthorized_returnsDeclinedStatus() {
    BankResponse response = new BankResponse(false, "");

    assertEquals(PaymentStatus.DECLINED, response.toPaymentStatus());
  }

  @Test
  void bankResponse_whenAuthorized_usesAuthorizationCodeAsPaymentId() {
    UUID authCode = UUID.randomUUID();
    BankResponse response = new BankResponse(true, authCode.toString());

    assertEquals(authCode, response.toPaymentId());
  }

  @Test
  void bankResponse_whenDeclined_generatesRandomPaymentId() {
    BankResponse response = new BankResponse(false, "");

    assertNotNull(response.toPaymentId());
  }

  @Test
  void bankResponse_whenAuthorizationCodeIsNull_normalisedToEmpty() {
    BankResponse response = new BankResponse(false, null);

    assertEquals("", response.authorizationCode());
  }

  @Test
  void bankResponse_whenAuthorizationCodeIsMalformed_generatesRandomPaymentId() {
    BankResponse response = new BankResponse(true, "not-a-valid-uuid");

    assertNotNull(response.toPaymentId());
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
}

package com.checkout.payment.gateway.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.checkout.payment.gateway.client.AcquiringBankClient;
import com.checkout.payment.gateway.client.BankRequest;
import com.checkout.payment.gateway.client.BankResponse;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.mapper.BankRequestMapper;
import com.checkout.payment.gateway.mapper.PaymentRequestMapper;
import com.checkout.payment.gateway.mapper.PaymentResponseMapper;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayServiceTest {

  @Mock private PaymentsRepository paymentsRepository;
  @Mock private AcquiringBankClient bankClient;
  @Mock private BankRequestMapper bankRequestMapper;
  @Mock private PaymentRequestMapper paymentRequestMapper;
  @Mock private PaymentResponseMapper paymentResponseMapper;

  @InjectMocks private PaymentGatewayService paymentService;

  @Test
  void processPayment_WhenBankAuthorizes_UsesBankAuthCodeAsId() {
    PostPaymentRequest request = new PostPaymentRequest();
    String bankAuthCode = UUID.randomUUID().toString();

    BankResponse bankResponse = new BankResponse();
    bankResponse.setAuthorized(true);
    bankResponse.setAuthorizationCode(bankAuthCode);

    when(bankRequestMapper.toBankRequest(request)).thenReturn(new BankRequest());
    when(bankClient.processBankPayment(any())).thenReturn(bankResponse);
    when(paymentResponseMapper.toPostResponse(any())).thenReturn(new PostPaymentResponse());

    paymentService.processPayment(request);

    ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);
    verify(paymentRequestMapper).create(eq(request), eq(PaymentStatus.AUTHORIZED), uuidCaptor.capture());
    assertEquals(UUID.fromString(bankAuthCode), uuidCaptor.getValue());
  }

  @Test
  void processPayment_WhenBankResponseIsNull_ReturnsDeclined() {
    PostPaymentRequest request = new PostPaymentRequest();

    when(bankRequestMapper.toBankRequest(request)).thenReturn(new BankRequest());
    when(bankClient.processBankPayment(any())).thenReturn(null);
    when(paymentResponseMapper.toPostResponse(any())).thenReturn(new PostPaymentResponse());

    paymentService.processPayment(request);

    verify(paymentRequestMapper).create(eq(request), eq(PaymentStatus.DECLINED), any(UUID.class));
  }

  @Test
  void processPayment_WhenAuthCodeIsNull_GeneratesRandomUuid() {
    PostPaymentRequest request = new PostPaymentRequest();

    BankResponse bankResponse = new BankResponse();
    bankResponse.setAuthorized(true);
    bankResponse.setAuthorizationCode(null);

    when(bankRequestMapper.toBankRequest(request)).thenReturn(new BankRequest());
    when(bankClient.processBankPayment(any())).thenReturn(bankResponse);
    when(paymentResponseMapper.toPostResponse(any())).thenReturn(new PostPaymentResponse());

    paymentService.processPayment(request);

    ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);
    verify(paymentRequestMapper).create(eq(request), eq(PaymentStatus.AUTHORIZED), uuidCaptor.capture());
    assertNotNull(uuidCaptor.getValue());
  }
}

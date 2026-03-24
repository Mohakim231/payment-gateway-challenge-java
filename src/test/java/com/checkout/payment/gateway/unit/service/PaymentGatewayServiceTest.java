package com.checkout.payment.gateway.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.checkout.payment.gateway.client.AcquiringBankClient;
import com.checkout.payment.gateway.client.BankRequest;
import com.checkout.payment.gateway.client.BankResponse;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.BankProcessingException;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import com.checkout.payment.gateway.service.mapper.BankRequestMapper;
import com.checkout.payment.gateway.service.mapper.PaymentRequestMapper;
import com.checkout.payment.gateway.model.response.GetPaymentResponse;
import com.checkout.payment.gateway.model.request.PostPaymentRequest;
import com.checkout.payment.gateway.model.response.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gateway.repository.entity.PaymentEntity;
import java.util.Optional;
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

  @InjectMocks private PaymentGatewayService paymentService;

  @Test
  void processPayment_whenBankAuthorizes_returnsAuthorizedStatus() {
    UUID authCode = UUID.randomUUID();
    PostPaymentRequest request = buildPostPaymentRequest();
    BankRequest bankRequest = buildBankRequest();
    BankResponse bankResponse = new BankResponse(true, authCode.toString());
    PaymentEntity entity = buildPaymentEntity(PaymentStatus.AUTHORIZED);

    when(bankRequestMapper.from(request)).thenReturn(bankRequest);
    when(bankClient.processBankPayment(bankRequest)).thenReturn(bankResponse);
    when(paymentRequestMapper.from(eq(request), eq(PaymentStatus.AUTHORIZED), eq(authCode)))
        .thenReturn(entity);

    PostPaymentResponse response = paymentService.processPayment(request);

    assertNotNull(response);
    assertEquals(PaymentStatus.AUTHORIZED, response.status());
    verify(paymentsRepository).add(entity);
  }

  @Test
  void processPayment_whenBankAuthorizes_usesBankAuthCodeAsPaymentId() {
    UUID authCode = UUID.randomUUID();
    PostPaymentRequest request = buildPostPaymentRequest();
    BankRequest bankRequest = buildBankRequest();
    BankResponse bankResponse = new BankResponse(true, authCode.toString());
    PaymentEntity entity = buildPaymentEntity(PaymentStatus.AUTHORIZED);

    when(bankRequestMapper.from(request)).thenReturn(bankRequest);
    when(bankClient.processBankPayment(bankRequest)).thenReturn(bankResponse);
    when(paymentRequestMapper.from(any(), any(), any())).thenReturn(entity);

    paymentService.processPayment(request);

    ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);
    verify(paymentRequestMapper).from(eq(request), eq(PaymentStatus.AUTHORIZED), uuidCaptor.capture());
    assertEquals(authCode, uuidCaptor.getValue());
  }

  @Test
  void processPayment_whenBankDeclines_returnsDeclinedStatus() {
    PostPaymentRequest request = buildPostPaymentRequest();
    BankRequest bankRequest = buildBankRequest();
    BankResponse bankResponse = new BankResponse(false, "");
    PaymentEntity entity = buildPaymentEntity(PaymentStatus.DECLINED);

    when(bankRequestMapper.from(request)).thenReturn(bankRequest);
    when(bankClient.processBankPayment(bankRequest)).thenReturn(bankResponse);
    when(paymentRequestMapper.from(eq(request), eq(PaymentStatus.DECLINED), any(UUID.class)))
        .thenReturn(entity);

    PostPaymentResponse response = paymentService.processPayment(request);

    assertNotNull(response);
    assertEquals(PaymentStatus.DECLINED, response.status());
    verify(paymentsRepository).add(entity);
  }

  @Test
  void processPayment_whenBankDeclines_generatesRandomPaymentId() {
    PostPaymentRequest request = buildPostPaymentRequest();
    BankRequest bankRequest = buildBankRequest();
    BankResponse bankResponse = new BankResponse(false, "");
    PaymentEntity entity = buildPaymentEntity(PaymentStatus.DECLINED);

    when(bankRequestMapper.from(request)).thenReturn(bankRequest);
    when(bankClient.processBankPayment(bankRequest)).thenReturn(bankResponse);
    when(paymentRequestMapper.from(any(), any(), any())).thenReturn(entity);

    paymentService.processPayment(request);

    ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);
    verify(paymentRequestMapper).from(eq(request), eq(PaymentStatus.DECLINED), uuidCaptor.capture());
    assertNotNull(uuidCaptor.getValue());
  }

  @Test
  void processPayment_whenBankClientThrows_propagatesBankProcessingException() {
    PostPaymentRequest request = buildPostPaymentRequest();
    BankRequest bankRequest = buildBankRequest();

    when(bankRequestMapper.from(request)).thenReturn(bankRequest);
    when(bankClient.processBankPayment(bankRequest))
        .thenThrow(new BankProcessingException("Bank unavailable"));

    assertThrows(BankProcessingException.class, () -> paymentService.processPayment(request));

    verify(paymentsRepository, never()).add(any());
  }

  @Test
  void processPayment_savesEntityToRepository() {
    UUID authCode = UUID.randomUUID();
    PostPaymentRequest request = buildPostPaymentRequest();
    BankRequest bankRequest = buildBankRequest();
    BankResponse bankResponse = new BankResponse(true, authCode.toString());
    PaymentEntity entity = buildPaymentEntity(PaymentStatus.AUTHORIZED);

    when(bankRequestMapper.from(request)).thenReturn(bankRequest);
    when(bankClient.processBankPayment(bankRequest)).thenReturn(bankResponse);
    when(paymentRequestMapper.from(any(), any(), any())).thenReturn(entity);

    paymentService.processPayment(request);

    verify(paymentsRepository, times(1)).add(entity);
  }

  @Test
  void getPaymentById_whenPaymentExists_returnsPaymentResponse() {
    UUID id = UUID.randomUUID();
    PaymentEntity entity = buildPaymentEntity(PaymentStatus.AUTHORIZED);

    when(paymentsRepository.get(id)).thenReturn(Optional.of(entity));

    GetPaymentResponse response = paymentService.getPaymentById(id);

    assertNotNull(response);
    assertEquals(PaymentStatus.AUTHORIZED, response.status());
  }

  @Test
  void getPaymentById_whenPaymentDoesNotExist_throwsEventProcessingException() {
    UUID id = UUID.randomUUID();

    when(paymentsRepository.get(id)).thenReturn(Optional.empty());

    assertThrows(EventProcessingException.class, () -> paymentService.getPaymentById(id));
  }

  private PostPaymentRequest buildPostPaymentRequest() {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("12345678901234");
    request.setExpiryMonth(12);
    request.setExpiryYear(2030);
    request.setCurrency("GBP");
    request.setAmount(500L);
    request.setCvv("123");
    return request;
  }

  private BankRequest buildBankRequest() {
    return new BankRequest("12345678901234", "12/2030", "GBP", 500L, "123");
  }

  private PaymentEntity buildPaymentEntity(PaymentStatus status) {
    return new PaymentEntity(
        UUID.randomUUID(),
        status,
        "1234",
        12,
        2030,
        "GBP",
        500L
    );
  }
}

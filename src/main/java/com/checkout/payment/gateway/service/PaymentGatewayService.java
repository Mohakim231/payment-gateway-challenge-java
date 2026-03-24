package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.client.AcquiringBankClient;
import com.checkout.payment.gateway.client.BankRequest;
import com.checkout.payment.gateway.client.BankResponse;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.mapper.BankRequestMapper;
import com.checkout.payment.gateway.mapper.PaymentRequestMapper;
import com.checkout.payment.gateway.mapper.PaymentResponseMapper;
import com.checkout.payment.gateway.model.GetPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import com.checkout.payment.gateway.repository.entity.PaymentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService implements PaymentService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final AcquiringBankClient bankClient;
  private final BankRequestMapper bankRequestMapper;
  private final PaymentRequestMapper paymentRequestMapper;
  private final PaymentResponseMapper paymentResponseMapper;

  public PaymentGatewayService(PaymentsRepository paymentsRepository, AcquiringBankClient bankClient, BankRequestMapper bankRequestMapper,
      PaymentRequestMapper paymentRequestMapper,
      PaymentResponseMapper paymentResponseMapper) {
    this.paymentsRepository = paymentsRepository;
    this.bankClient = bankClient;
    this.bankRequestMapper = bankRequestMapper;
    this.paymentRequestMapper = paymentRequestMapper;
    this.paymentResponseMapper = paymentResponseMapper;
  }

  @Override
  public GetPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to payment with ID {}", id);
    return paymentResponseMapper.toGetResponse(paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID")));
  }

  @Override
  public PostPaymentResponse processPayment(PostPaymentRequest paymentRequest) {
    BankRequest bankRequest = bankRequestMapper.toBankRequest(paymentRequest);
    BankResponse bankResponse = bankClient.processBankPayment(bankRequest);
    PaymentStatus finalStatus = (bankResponse != null && bankResponse.isAuthorized()) ? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED;
    String authCode = (bankResponse != null) ? bankResponse.getAuthorizationCode() : null;
    UUID paymentId = (authCode != null && !authCode.trim().isEmpty())
        ? UUID.fromString(authCode)
        : UUID.randomUUID();
    PaymentEntity entity = paymentRequestMapper.create(paymentRequest, finalStatus, paymentId);
    paymentsRepository.add(entity);
    return paymentResponseMapper.toPostResponse(entity);
  }
}

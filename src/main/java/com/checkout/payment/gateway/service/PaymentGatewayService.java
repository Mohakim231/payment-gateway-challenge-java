package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.client.AcquiringBankClient;
import com.checkout.payment.gateway.client.BankResponse;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.service.mapper.BankRequestMapper;
import com.checkout.payment.gateway.service.mapper.PaymentRequestMapper;
import com.checkout.payment.gateway.model.response.GetPaymentResponse;
import com.checkout.payment.gateway.model.request.PostPaymentRequest;
import com.checkout.payment.gateway.model.response.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentStore;
import com.checkout.payment.gateway.repository.entity.PaymentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class PaymentGatewayService implements PaymentService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentStore paymentStore;
  private final AcquiringBankClient bankClient;
  private final BankRequestMapper bankRequestMapper;
  private final PaymentRequestMapper paymentRequestMapper;

  public PaymentGatewayService(
      PaymentStore paymentStore,
      AcquiringBankClient bankClient,
      BankRequestMapper bankRequestMapper,
      PaymentRequestMapper paymentRequestMapper) {
    this.paymentStore = paymentStore;
    this.bankClient = bankClient;
    this.bankRequestMapper = bankRequestMapper;
    this.paymentRequestMapper = paymentRequestMapper;
  }

  @Override
  public GetPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to payment with ID {}", id);
    return GetPaymentResponse.from(
        paymentStore.get(id)
            .orElseThrow(() -> new EventProcessingException("Invalid ID"))
    );
  }

  @Override
  public PostPaymentResponse processPayment(PostPaymentRequest request) {
    BankResponse bankResponse = bankClient.processBankPayment(bankRequestMapper.from(request));

    PaymentEntity entity = paymentRequestMapper.from(
        request,
        bankResponse.toPaymentStatus(),
        bankResponse.toPaymentId()
    );

    paymentStore.add(entity);
    return PostPaymentResponse.from(entity);
  }
}

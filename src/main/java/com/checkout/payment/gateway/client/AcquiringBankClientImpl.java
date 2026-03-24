package com.checkout.payment.gateway.client;

import com.checkout.payment.gateway.exception.BankProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.checkout.payment.gateway.constant.Constants.ACQUIRING_BANK_PROCESS_URL;

@Component
public class AcquiringBankClientImpl implements AcquiringBankClient {

  private static final Logger LOG = LoggerFactory.getLogger(AcquiringBankClientImpl.class);

  private final String bankSimulatorUrl;
  private final RestTemplate restTemplate;

  public AcquiringBankClientImpl(RestTemplate restTemplate, @Value("${bank.simulator.base.url}") String bankSimulatorUrl) {
    this.restTemplate = restTemplate;
    this.bankSimulatorUrl = bankSimulatorUrl;
  }

  @Override
  public BankResponse processBankPayment(BankRequest bankRequest) {
    try {
      return restTemplate.postForEntity(
          bankSimulatorUrl + ACQUIRING_BANK_PROCESS_URL,
          bankRequest,
          BankResponse.class
      ).getBody();

    } catch (RestClientException e) {
      LOG.error("Network or connection error reaching Acquiring Bank: {}", e.getMessage());
      throw new BankProcessingException("Your payment could not be processed at this time. Please try again later.");
    }
  }
}

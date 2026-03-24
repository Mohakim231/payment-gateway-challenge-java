package com.checkout.payment.gateway.client;

import com.checkout.payment.gateway.exception.BankProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BankResponseErrorHandler implements ResponseErrorHandler {
  private static final Logger LOG = LoggerFactory.getLogger(BankResponseErrorHandler.class);

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
  }

  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);

    LOG.error("Acquiring bank rejected the request. Status Code: {}, Response Body: {}",
        response.getStatusCode(), responseBody);

    throw new BankProcessingException("Your payment could not be processed at this time. Please try again later.");
  }
}

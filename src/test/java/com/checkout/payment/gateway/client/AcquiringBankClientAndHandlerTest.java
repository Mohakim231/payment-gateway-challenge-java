package com.checkout.payment.gateway.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.IOException;
import com.checkout.payment.gateway.exception.BankProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class AcquiringBankClientAndHandlerTest {

  @Mock private RestTemplate restTemplate;
  @Mock private ClientHttpResponse clientHttpResponse;

  private AcquiringBankClientImpl bankClient;
  private BankResponseErrorHandler errorHandler;

  @BeforeEach
  void setUp() {
    bankClient = new AcquiringBankClientImpl(restTemplate, "http://localhost:8080");
    errorHandler = new BankResponseErrorHandler();
  }

  @Test
  void processBankPayment_Success_ReturnsResponse() {
    BankRequest request = new BankRequest();
    BankResponse mockResponse = new BankResponse();
    mockResponse.setAuthorized(true);

    when(restTemplate.postForEntity(eq("http://localhost:8080/payments"), eq(request), eq(BankResponse.class)))
        .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

    BankResponse response = bankClient.processBankPayment(request);

    assertTrue(response.isAuthorized());
  }

  @Test
  void processBankPayment_NetworkError_ThrowsBankServiceUnavailableException() {
    BankRequest request = new BankRequest();
    when(restTemplate.postForEntity(anyString(), any(), any()))
        .thenThrow(new RestClientException("Connection refused"));

    assertThrows(BankProcessingException.class, () -> bankClient.processBankPayment(request));
  }

  @Test
  void errorHandler_HasError_ReturnsTrueFor4xxAnd5xx() throws IOException {
    when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
    assertTrue(errorHandler.hasError(clientHttpResponse));

    when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    assertTrue(errorHandler.hasError(clientHttpResponse));

    when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.OK);
    assertFalse(errorHandler.hasError(clientHttpResponse));
  }

  @Test
  void processBankPayment_NetworkError_ThrowsBankProcessingException() {
    BankRequest request = new BankRequest();
    when(restTemplate.postForEntity(anyString(), any(), any()))
        .thenThrow(new RestClientException("Connection refused"));

    assertThrows(BankProcessingException.class, () -> bankClient.processBankPayment(request));
  }

  @Test
  void errorHandler_HandleError_ThrowsGenericBankProcessingException() throws IOException {
    when(clientHttpResponse.getBody()).thenReturn(new java.io.ByteArrayInputStream("Bank error details".getBytes()));
    when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

    BankProcessingException exception = assertThrows(BankProcessingException.class,
        () -> errorHandler.handleError(clientHttpResponse));

    assertEquals("Your payment could not be processed at this time. Please try again later.", exception.getMessage());
  }
}

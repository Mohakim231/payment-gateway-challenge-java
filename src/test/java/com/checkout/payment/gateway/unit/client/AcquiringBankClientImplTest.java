package com.checkout.payment.gateway.unit.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.checkout.payment.gateway.client.AcquiringBankClientImpl;
import com.checkout.payment.gateway.client.BankRequest;
import com.checkout.payment.gateway.client.BankResponse;
import com.checkout.payment.gateway.exception.BankProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class AcquiringBankClientImplTest {

  @Mock private RestTemplate restTemplate;

  private AcquiringBankClientImpl bankClient;

  @BeforeEach
  void setUp() {
    bankClient = new AcquiringBankClientImpl(restTemplate, "http://localhost:8080");
  }

  @Test
  void processBankPayment_whenBankAuthorizes_returnsAuthorizedResponse() {
    BankRequest request = buildBankRequest();
    BankResponse mockResponse = new BankResponse(true, "550e8400-e29b-41d4-a716-446655440000");

    when(restTemplate.postForEntity(
        eq("http://localhost:8080/payments"),
        eq(request),
        eq(BankResponse.class)))
        .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

    BankResponse response = bankClient.processBankPayment(request);

    assertTrue(response.authorized());
    assertEquals("550e8400-e29b-41d4-a716-446655440000", response.authorizationCode());
  }

  @Test
  void processBankPayment_whenBankDeclines_returnsDeclinedResponse() {
    BankRequest request = buildBankRequest();
    BankResponse mockResponse = new BankResponse(false, "");

    when(restTemplate.postForEntity(
        eq("http://localhost:8080/payments"),
        eq(request),
        eq(BankResponse.class)))
        .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

    BankResponse response = bankClient.processBankPayment(request);

    assertFalse(response.authorized());
    assertEquals("", response.authorizationCode());
  }

  @Test
  void processBankPayment_whenNetworkError_throwsBankProcessingException() {
    BankRequest request = buildBankRequest();

    when(restTemplate.postForEntity(anyString(), any(), any()))
        .thenThrow(new RestClientException("Connection refused"));

    assertThrows(BankProcessingException.class, () -> bankClient.processBankPayment(request));
  }

  @Test
  void processBankPayment_whenNetworkError_doesNotExposeInternalDetails() {
    BankRequest request = buildBankRequest();

    when(restTemplate.postForEntity(anyString(), any(), any()))
        .thenThrow(new RestClientException("Connection refused"));

    BankProcessingException exception = assertThrows(
        BankProcessingException.class,
        () -> bankClient.processBankPayment(request)
    );

    assertEquals(
        "Your payment could not be processed at this time. Please try again later.",
        exception.getMessage()
    );
  }

  private BankRequest buildBankRequest() {
    return new BankRequest("1234567890123456", "12/2030", "GBP", 500L, "123");
  }
}

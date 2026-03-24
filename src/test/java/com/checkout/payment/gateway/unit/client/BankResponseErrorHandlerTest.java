package com.checkout.payment.gateway.unit.clients;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.checkout.payment.gateway.client.BankResponseErrorHandler;
import com.checkout.payment.gateway.exception.BankProcessingException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

@ExtendWith(MockitoExtension.class)
class BankResponseErrorHandlerTest {

  @Mock private ClientHttpResponse clientHttpResponse;

  private BankResponseErrorHandler errorHandler;

  @BeforeEach
  void setUp() {
    errorHandler = new BankResponseErrorHandler();
  }

  @Test
  void hasError_whenResponseIs4xx_returnsTrue() throws IOException {
    when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

    assertTrue(errorHandler.hasError(clientHttpResponse));
  }

  @Test
  void hasError_whenResponseIs5xx_returnsTrue() throws IOException {
    when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

    assertTrue(errorHandler.hasError(clientHttpResponse));
  }

  @Test
  void hasError_whenResponseIs2xx_returnsFalse() throws IOException {
    when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.OK);

    assertFalse(errorHandler.hasError(clientHttpResponse));
  }

  @Test
  void handleError_whenErrorOccurs_throwsGenericBankProcessingException() throws IOException {
    when(clientHttpResponse.getBody())
        .thenReturn(new ByteArrayInputStream("Bank error details".getBytes()));
    when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

    BankProcessingException exception = assertThrows(
        BankProcessingException.class,
        () -> errorHandler.handleError(clientHttpResponse)
    );

    assertEquals(
        "Your payment could not be processed at this time. Please try again later.",
        exception.getMessage()
    );
  }

  @Test
  void handleError_whenErrorOccurs_doesNotExposeRawBankErrorDetails() throws IOException {
    String sensitiveMessage = "Internal bank error — account 1234 declined";

    when(clientHttpResponse.getBody())
        .thenReturn(new ByteArrayInputStream(sensitiveMessage.getBytes()));
    when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

    BankProcessingException exception = assertThrows(
        BankProcessingException.class,
        () -> errorHandler.handleError(clientHttpResponse)
    );

    assertFalse(exception.getMessage().contains(sensitiveMessage));
    assertEquals(
        "Your payment could not be processed at this time. Please try again later.",
        exception.getMessage()
    );
  }
}

package com.checkout.payment.gateway.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.client.AcquiringBankClient;
import com.checkout.payment.gateway.client.BankResponse;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.BankProcessingException;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.time.YearMonth;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private AcquiringBankClient bankClient;

  @Test
  void processPaymentAndRetrieve_EndToEnd() throws Exception {
    PostPaymentRequest request = createValidRequest();

    BankResponse bankResponse = new BankResponse();
    bankResponse.setAuthorized(true);
    bankResponse.setAuthorizationCode(UUID.randomUUID().toString());
    when(bankClient.processBankPayment(any())).thenReturn(bankResponse);

    MvcResult postResult = mvc.perform(post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()))
        .andExpect(jsonPath("$.last_four_card_digits").value("3456"))
        .andExpect(jsonPath("$.expiry_month").value(12))
        .andExpect(jsonPath("$.expiry_year").value(request.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value("USD"))
        .andExpect(jsonPath("$.amount").value(1000))
        .andReturn();

    String responseJson = postResult.getResponse().getContentAsString();
    String paymentId = JsonPath.read(responseJson, "$.id");

    mvc.perform(get("/payment/" + paymentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(paymentId))
        .andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()))
        .andExpect(jsonPath("$.last_four_card_digits").value("3456"))
        .andExpect(jsonPath("$.expiry_month").value(12))
        .andExpect(jsonPath("$.expiry_year").value(request.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value("USD"))
        .andExpect(jsonPath("$.amount").value(1000))
        .andExpect(jsonPath("$.card_number").doesNotExist());
  }

  @Test
  void processPayment_WhenBankDeclines_ReturnsOkAndDeclined() throws Exception {
    PostPaymentRequest request = createValidRequest();

    BankResponse bankResponse = new BankResponse();
    bankResponse.setAuthorized(false);
    when(bankClient.processBankPayment(any())).thenReturn(bankResponse);

    mvc.perform(post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.status").value(PaymentStatus.DECLINED.getName()))
        .andExpect(jsonPath("$.last_four_card_digits").value("3456"))
        .andExpect(jsonPath("$.currency").value("USD"));
  }

  @ParameterizedTest(name = "Invalid Request: {1}")
  @MethodSource("provideInvalidRequests")
  void processPayment_WithInvalidData_ReturnsRejectedAndSpecificMessage(PostPaymentRequest invalidRequest, String scenario, String expectedError) throws Exception {
    mvc.perform(post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(PaymentStatus.REJECTED.getName()))
        .andExpect(jsonPath("$.errors", hasItem(containsString(expectedError))));
  }

  static Stream<Arguments> provideInvalidRequests() {
    int pastYear = YearMonth.now().getYear() - 1;

    PostPaymentRequest nullCard = createValidRequest(); nullCard.setCardNumber(null);
    PostPaymentRequest shortCard = createValidRequest(); shortCard.setCardNumber("1234567890123");
    PostPaymentRequest zeroMonth = createValidRequest(); zeroMonth.setExpiryMonth(0);
    PostPaymentRequest pastExpiry = createValidRequest(); pastExpiry.setExpiryYear(pastYear);
    PostPaymentRequest nullAmount = createValidRequest(); nullAmount.setAmount(null);
    PostPaymentRequest zeroAmount = createValidRequest(); zeroAmount.setAmount(0L);

    return Stream.of(
        Arguments.of(nullCard, "Null card number", "Card number is required"),
        Arguments.of(shortCard, "Card number too short", "14-19 numeric characters"),
        Arguments.of(zeroMonth, "Expiry month = 0", "must be at least 1"),
        Arguments.of(pastExpiry, "Past expiry date", "Expiry date must be in the future"),
        Arguments.of(nullAmount, "Null amount", "must not be null"),
        Arguments.of(zeroAmount, "Zero amount", "must be greater than 0")
    );
  }

  @Test
  void processPayment_WhenBankFails_Returns500AndGenericMessage() throws Exception {
    PostPaymentRequest request = createValidRequest();
    String genericMessage = "Your payment could not be processed at this time. Please try again later.";

    when(bankClient.processBankPayment(any())).thenThrow(new BankProcessingException(genericMessage));

    mvc.perform(post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value(genericMessage))
        .andExpect(jsonPath("$.error").doesNotExist());
  }

  @Test
  void getPayment_WhenDoesNotExist_Returns404() throws Exception {
    mvc.perform(get("/payment/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Record not found"));
  }

  private static PostPaymentRequest createValidRequest() {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("1234567890123456");
    request.setExpiryMonth(12);
    request.setExpiryYear(YearMonth.now().getYear() + 1);
    request.setCurrency("USD");
    request.setAmount(1000L);
    request.setCvv("123");
    return request;
  }
}

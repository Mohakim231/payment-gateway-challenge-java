package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.model.GetPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import java.util.UUID;
import com.checkout.payment.gateway.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("api")
@RequestMapping("/payment")
public class PaymentGatewayController {

  private final PaymentService paymentService;

  public PaymentGatewayController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<GetPaymentResponse> getPaymentEventById(@PathVariable UUID id) {
    return new ResponseEntity<>(paymentService.getPaymentById(id), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<PostPaymentResponse> processPostPaymentEvent(@Valid @RequestBody PostPaymentRequest postPaymentRequest ) {
    return new ResponseEntity<>(paymentService.processPayment(postPaymentRequest), HttpStatus.OK);
  }
}

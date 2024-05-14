package dev.steampunkpayment.controller;

import dev.steampunkpayment.dto.request.PaymentAddRequest;
import dev.steampunkpayment.dto.response.PaymentAddResponse;
import dev.steampunkpayment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentAddResponse> addPayment(@RequestBody PaymentAddRequest request) {
        PaymentAddResponse res = paymentService.addPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}

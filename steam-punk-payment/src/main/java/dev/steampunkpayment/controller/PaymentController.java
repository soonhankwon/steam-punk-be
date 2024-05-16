package dev.steampunkpayment.controller;

import dev.steampunkpayment.dto.request.PaymentAddRequest;
import dev.steampunkpayment.dto.response.PaymentAddResponse;
import dev.steampunkpayment.dto.response.PaymentGetResponse;
import dev.steampunkpayment.dto.response.RefundProgressAddResponse;
import dev.steampunkpayment.dto.response.RefundProgressGetResponse;
import dev.steampunkpayment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("{paymentId}")
    public ResponseEntity<PaymentGetResponse> getPayment(@PathVariable Long paymentId) {
        PaymentGetResponse res = paymentService.findPayment(paymentId);
        return ResponseEntity.ok(res);
    }

    @PatchMapping("{paymentId}/refund")
    public ResponseEntity<RefundProgressAddResponse> addRefundInProgress(@PathVariable Long paymentId) {
        RefundProgressAddResponse res = paymentService.addRefundInProgress(paymentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("{paymentId}/refund")
    public ResponseEntity<RefundProgressGetResponse> getRefundInProgress(@PathVariable Long paymentId) {
        RefundProgressGetResponse res = paymentService.findRefundInProgress(paymentId);
        return ResponseEntity.ok(res);
    }
}

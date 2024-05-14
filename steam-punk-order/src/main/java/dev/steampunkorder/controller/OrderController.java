package dev.steampunkorder.controller;

import dev.steampunkorder.dto.request.OrderAddRequest;
import dev.steampunkorder.dto.response.OrderAddResponse;
import dev.steampunkorder.dto.response.OrderGetResponse;
import dev.steampunkorder.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userId}")
    public ResponseEntity<OrderAddResponse> addOrder(@PathVariable Long userId,
                                                     @RequestBody OrderAddRequest request) {
        OrderAddResponse res = orderService.addOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderGetResponse> getOrder(@PathVariable Long orderId) {
        OrderGetResponse res = orderService.findOrder(orderId);
        return ResponseEntity.ok().body(res);
    }
}
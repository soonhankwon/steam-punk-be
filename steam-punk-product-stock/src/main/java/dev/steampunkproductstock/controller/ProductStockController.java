package dev.steampunkproductstock.controller;

import dev.steampunkproductstock.dto.request.ProductStockAddRequest;
import dev.steampunkproductstock.dto.response.ProductStockAddResponse;
import dev.steampunkproductstock.dto.response.ProductStockGetResponse;
import dev.steampunkproductstock.service.StockService;
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
@RequestMapping("/api/v1/stock")
public class ProductStockController {

    private final StockService stockService;

    @PostMapping
    public ResponseEntity<ProductStockAddResponse> addProductStock(@RequestBody ProductStockAddRequest request) {
        ProductStockAddResponse res = stockService.addProductStock(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(res);
    }

    @GetMapping("{productId}")
    public ResponseEntity<ProductStockGetResponse> getProductStock(@PathVariable Long productId) {
        ProductStockGetResponse res = stockService.findProductStock(productId);
        return ResponseEntity.ok(res);
    }

    @PatchMapping("{productId}")
    public ResponseEntity<ProductStockGetResponse> decreaseProductStock(@PathVariable Long productId) {
        ProductStockGetResponse res = stockService.decreaseProductStock(productId);
        return ResponseEntity.ok(res);
    }

    @PatchMapping("{productId}/increase")
    public ResponseEntity<ProductStockGetResponse> increaseProductStock(@PathVariable Long productId) {
        ProductStockGetResponse res = stockService.increaseProductStock(productId);
        return ResponseEntity.ok(res);
    }
}

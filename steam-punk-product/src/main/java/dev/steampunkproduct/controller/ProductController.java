package dev.steampunkproduct.controller;

import dev.steampunkproduct.dto.response.ProductGetResponse;
import dev.steampunkproduct.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("{productId}")
    public ResponseEntity<ProductGetResponse> getProduct(@PathVariable Long productId) {
        ProductGetResponse res = productService.getProduct(productId);
        return ResponseEntity.ok(res);
    }
}

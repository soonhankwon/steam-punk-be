package dev.steampunkproduct.controller;

import dev.steampunkproduct.dto.request.ProductAddRequest;
import dev.steampunkproduct.dto.response.ProductAddResponse;
import dev.steampunkproduct.dto.response.ProductExistsCheckResponse;
import dev.steampunkproduct.dto.response.ProductGetResponse;
import dev.steampunkproduct.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductAddResponse> addProduct(@RequestBody ProductAddRequest request) {
        ProductAddResponse res = productService.addProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("{productId}")
    public ResponseEntity<ProductGetResponse> getProduct(@PathVariable Long productId) {
        ProductGetResponse res = productService.getProduct(productId);
        return ResponseEntity.ok(res);
    }

    @GetMapping("{productId}/check")
    public ResponseEntity<ProductExistsCheckResponse> checkProductExists(@PathVariable Long productId) {
        ProductExistsCheckResponse res = productService.checkProductExists(productId);
        return ResponseEntity.ok(res);
    }

    @GetMapping
    public ResponseEntity<List<ProductGetResponse>> getProducts(@RequestParam(value = "page_number") int pageNumber) {
        List<ProductGetResponse> res = productService.getProducts(pageNumber);
        return ResponseEntity.ok(res);
    }
}

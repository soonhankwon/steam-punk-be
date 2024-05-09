package dev.steampunkproduct.service;

import dev.steampunkproduct.domain.Product;
import dev.steampunkproduct.dto.response.ProductGetResponse;
import dev.steampunkproduct.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public ProductGetResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("no product Id"));
        return ProductGetResponse.from(product);
    }
}

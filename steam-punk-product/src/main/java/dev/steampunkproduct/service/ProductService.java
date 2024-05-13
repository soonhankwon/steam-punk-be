package dev.steampunkproduct.service;

import dev.steampunkproduct.domain.Product;
import dev.steampunkproduct.dto.response.ProductGetResponse;
import dev.steampunkproduct.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private static final int PAGE_SIZE = 10;

    @Transactional(readOnly = true)
    public ProductGetResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("no product Id"));
        return ProductGetResponse.from(product);
    }

    @Transactional(readOnly = true)
    public List<ProductGetResponse> getProducts(int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, PAGE_SIZE);
        return productRepository.findAll(pageRequest)
                .stream()
                .map(ProductGetResponse::from)
                .collect(Collectors.toList());
    }
}

package dev.steampunkproduct.service;

import dev.steampunkproduct.common.enumtype.ErrorCode;
import dev.steampunkproduct.common.exception.ApiException;
import dev.steampunkproduct.domain.Product;
import dev.steampunkproduct.domain.ProductState;
import dev.steampunkproduct.dto.request.ProductAddRequest;
import dev.steampunkproduct.dto.request.ProductStockAddRequest;
import dev.steampunkproduct.dto.response.ProductAddResponse;
import dev.steampunkproduct.dto.response.ProductExistsCheckResponse;
import dev.steampunkproduct.dto.response.ProductGetResponse;
import dev.steampunkproduct.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private static final int PAGE_SIZE = 10;

    @Transactional(readOnly = true)
    public ProductGetResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_PRODUCT_ID));
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

    @Transactional(readOnly = true)
    public ProductExistsCheckResponse checkProductExists(Long productId) {
        boolean isExists = productRepository.existsById(productId);
        return ProductExistsCheckResponse.from(isExists);
    }

    @Transactional
    public ProductAddResponse addProduct(ProductAddRequest request) {
        Product product = Product.from(request);
        ProductState requestProductState = request.productState();
        product = productRepository.save(product);
        // 한정수량세일 또는 한정수량 이벤트 상품은 실시간 재고 서비스 등록
        if (requestProductState == ProductState.ON_SALE_LIMITED_STOCK_EVENT
                || requestProductState == ProductState.LIMITED_STOCK_EVENT) {
            ProductStockAddRequest productStockAddRequest = ProductStockAddRequest.of(product.getId(),
                    request.productStockQuantity());
            addProductStock(productStockAddRequest);
        }
        return ProductAddResponse.from(product);
    }

    private void addProductStock(ProductStockAddRequest request) {
        WebClient.create()
                .post()
                .uri("http://localhost:8080/api/v1/stock")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}

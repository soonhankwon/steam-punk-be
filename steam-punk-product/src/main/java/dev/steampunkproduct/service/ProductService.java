package dev.steampunkproduct.service;

import dev.steampunkproduct.common.enumtype.ErrorCode;
import dev.steampunkproduct.common.exception.ApiException;
import dev.steampunkproduct.domain.Category;
import dev.steampunkproduct.domain.Product;
import dev.steampunkproduct.domain.ProductCategory;
import dev.steampunkproduct.domain.ProductState;
import dev.steampunkproduct.dto.request.ProductAddRequest;
import dev.steampunkproduct.dto.request.ProductStockAddRequest;
import dev.steampunkproduct.dto.response.ProductAddResponse;
import dev.steampunkproduct.dto.response.ProductExistsCheckResponse;
import dev.steampunkproduct.dto.response.ProductGetResponse;
import dev.steampunkproduct.dto.response.ProductsGetResponse;
import dev.steampunkproduct.dto.response.ProductsGetResponse.ProductsMetaData;
import dev.steampunkproduct.repository.CategoryRepository;
import dev.steampunkproduct.repository.ProductCategoryRepository;
import dev.steampunkproduct.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private static final int PAGE_SIZE = 10;

    @Transactional(readOnly = true)
    public ProductGetResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_PRODUCT_ID));

        List<ProductCategory> productCategories = productCategoryRepository.findAllByProduct(product);
        List<String> categories = new ArrayList<>();
        productCategories.forEach(i -> {
            Category category = categoryRepository.findById(i.getCategoryId())
                    .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_CATEGORY_ID));
            categories.add(category.getName());
        });

        return ProductGetResponse.of(product, categories);
    }

    @Transactional(readOnly = true)
    public ProductsGetResponse getProducts(int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, PAGE_SIZE);
        Page<Product> productPage = productRepository.findAll(pageRequest);
        ProductsMetaData metaData = new ProductsMetaData(
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );

        List<Product> products = productPage.stream().toList();
        List<ProductGetResponse> res = new ArrayList<>();
        products.forEach(p -> {
            List<ProductCategory> productCategories = productCategoryRepository.findAllByProduct(p);
            List<String> categories = new ArrayList<>();
            productCategories.forEach(pc -> {
                Category category = categoryRepository.findById(pc.getCategoryId())
                        .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_CATEGORY_ID));
                categories.add(category.getName());
            });
            res.add(ProductGetResponse.of(p, categories));
        });
        return ProductsGetResponse.of(metaData, res);
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
        Product finalProduct = product;
        request.categories()
                .forEach(c -> {
                    Category category = categoryRepository.findByName(c)
                            .orElseGet(() -> categoryRepository.save(new Category(c)));
                    productCategoryRepository.save(
                            new ProductCategory(
                                    finalProduct,
                                    Objects.requireNonNull(category, "Category Id cannot be null").getId()
                            )
                    );
                });
        // 한정수량세일 또는 한정수량 이벤트 상품은 실시간 재고 서비스 등록
        if (requestProductState == ProductState.ON_SALE_LIMITED_STOCK_EVENT
                || requestProductState == ProductState.LIMITED_STOCK_EVENT) {
            ProductStockAddRequest productStockAddRequest = ProductStockAddRequest.of(product.getId(),
                    request.stockQuantity());
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

package dev.steampunkproduct.service;

import dev.steampunkproduct.common.enumtype.ErrorCode;
import dev.steampunkproduct.common.exception.ApiException;
import dev.steampunkproduct.domain.Category;
import dev.steampunkproduct.domain.Product;
import dev.steampunkproduct.domain.ProductCategory;
import dev.steampunkproduct.domain.ProductStockInfo;
import dev.steampunkproduct.dto.request.ProductAddRequest;
import dev.steampunkproduct.dto.request.ProductStockAddRequest;
import dev.steampunkproduct.dto.response.ProductAddResponse;
import dev.steampunkproduct.dto.response.ProductExistsCheckResponse;
import dev.steampunkproduct.dto.response.ProductGetResponse;
import dev.steampunkproduct.dto.response.ProductsGetResponse;
import dev.steampunkproduct.dto.response.ProductsGetResponse.ProductsMetaData;
import dev.steampunkproduct.enumtype.ProductState;
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

        ProductState productState = product.getProductState();
        ProductStockInfo productStockInfo = getProductStockInfoCanBeNull(productId, productState);
        List<ProductCategory> productCategories = productCategoryRepository.findAllByProduct(product);
        List<String> categories = getCategoriesString(productCategories);
        return ProductGetResponse.of(product, categories, productStockInfo);
    }

    // 상품이 한정 판매 또는 한정 세일 판매가 아닌 경우 null을 리턴함
    private ProductStockInfo getProductStockInfoCanBeNull(Long productId, ProductState productState) {
        ProductStockInfo productStockInfo = null;
        if (productState == ProductState.LIMITED_STOCK || productState == ProductState.ON_SALE_LIMITED_STOCK) {
            productStockInfo = getProductStock(productId);
        }
        return productStockInfo;
    }

    private ProductStockInfo getProductStock(Long productId) {
        return WebClient.create()
                .get()
                .uri("http://localhost:8080/api/v1/stock/{productId}", productId)
                .retrieve()
                .bodyToMono(ProductStockInfo.class)
                .block();
    }

    /**
     * @param productCategories 상품 카테고리 리스트
     * @return 카테고리 문자열 리스트
     */
    private List<String> getCategoriesString(List<ProductCategory> productCategories) {
        List<String> categories = new ArrayList<>();
        productCategories.forEach(pc -> {
            // 카테고리 레포지토리에서 조회 후 없으면 예외를 던짐
            Category category = categoryRepository.findById(pc.getCategoryId())
                    .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_CATEGORY_ID));
            categories.add(category.getName());
        });
        return categories;
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
            List<String> categories = getCategoriesString(productCategories);
            ProductState productState = p.getProductState();
            ProductStockInfo productStockInfo = getProductStockInfoCanBeNull(p.getId(), productState);
            res.add(ProductGetResponse.of(p, categories, productStockInfo));
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
        if (requestProductState == ProductState.ON_SALE_LIMITED_STOCK
                || requestProductState == ProductState.LIMITED_STOCK) {
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

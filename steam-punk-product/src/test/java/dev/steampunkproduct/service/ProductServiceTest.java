package dev.steampunkproduct.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import dev.steampunkproduct.common.exception.ApiException;
import dev.steampunkproduct.domain.Category;
import dev.steampunkproduct.domain.Product;
import dev.steampunkproduct.domain.ProductDiscountPolicy;
import dev.steampunkproduct.domain.ProductStockInfo;
import dev.steampunkproduct.dto.request.ProductAddRequest;
import dev.steampunkproduct.dto.response.ProductExistsCheckResponse;
import dev.steampunkproduct.dto.response.ProductGetResponse;
import dev.steampunkproduct.dto.response.ProductsGetResponse;
import dev.steampunkproduct.enumtype.ProductState;
import dev.steampunkproduct.repository.CategoryRepository;
import dev.steampunkproduct.repository.ProductCategoryRepository;
import dev.steampunkproduct.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 단위 테스트")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.from(new ProductAddRequest(
                "Test Game", 59.99, "Desc", "img.jpg", "site.com", "Dev",
                ProductState.ON_SALE, ProductDiscountPolicy.REGULAR, null, List.of()
        ));
    }

    @Nested
    @DisplayName("getProduct 메서드 테스트")
    class GetProductTest {

        @Test
        @DisplayName("상품 ID로 상품 조회에 성공한다 (재고 한정 상품 아님).")
        void getProductSuccess_NoLimitedStock() {
            // given
            given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
            given(productCategoryRepository.findAllByProduct(any(Product.class))).willReturn(List.of());

            // when
            ProductGetResponse response = productService.getProduct(1L);

            // then
            assertThat(response.name()).isEqualTo(product.getName());
            assertThat(response.stockQuantity()).isNull();
        }

        @Test
        @DisplayName("상품 ID로 상품 조회에 성공한다 (재고 한정 상품).")
        void getProductSuccess_LimitedStock() {
            // given
            Product limitedProduct = Product.from(new ProductAddRequest(
                    "Limited Game", 99.99, "Limited Desc", "l-img.jpg", "l-site.com", "L-Dev",
                    ProductState.LIMITED_STOCK, ProductDiscountPolicy.REGULAR, 100L, List.of()
            ));
            ProductStockInfo mockStockInfo = new ProductStockInfo(100L);

            given(productRepository.findById(anyLong())).willReturn(Optional.of(limitedProduct));
            given(productCategoryRepository.findAllByProduct(any(Product.class))).willReturn(List.of());

            // WebClient mocking
            WebClient webClient = mock(WebClient.class);
            WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
            WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

            try (MockedStatic<WebClient> mockedWebClient = mockStatic(WebClient.class)) {
                mockedWebClient.when(WebClient::create).thenReturn(webClient);
                given(webClient.get()).willReturn(requestHeadersUriSpec);
                given(requestHeadersUriSpec.uri("http://localhost:8080/api/v1/stock/{productId}", 1L))
                        .willReturn(requestHeadersUriSpec);
                given(requestHeadersUriSpec.retrieve()).willReturn(responseSpec);
                given(responseSpec.bodyToMono(ProductStockInfo.class)).willReturn(Mono.just(mockStockInfo));

                // when
                ProductGetResponse response = productService.getProduct(1L);

                // then
                assertThat(response.name()).isEqualTo(limitedProduct.getName());
                assertThat(response.stockQuantity()).isEqualTo(100L);
            }
        }

        @Test
        @DisplayName("존재하지 않는 상품 ID로 조회 시 예외가 발생한다.")
        void getProductFail_NotFound() {
            // given
            given(productRepository.findById(anyLong())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.getProduct(999L))
                    .isInstanceOf(ApiException.class)
                    .extracting(ex -> ((ApiException) ex).getErrorCode())
                    .isEqualTo(dev.steampunkproduct.common.enumtype.ErrorCode.NOT_EXISTS_PRODUCT_ID);
        }
    }

    @Test
    @DisplayName("상품 목록을 페이지별로 조회한다.")
    void getProducts() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageRequest, 1L);
        given(productRepository.findAll(pageRequest)).willReturn(productPage);
        given(productCategoryRepository.findAllByProduct(any(Product.class))).willReturn(List.of());

        // when
        ProductsGetResponse response = productService.getProducts(0);

        // then
        assertThat(response.meta().totalCount()).isEqualTo(1);
        assertThat(response.documents().size()).isEqualTo(1);
        assertThat(response.documents().get(0).name()).isEqualTo(product.getName());
    }

    @Test
    @DisplayName("상품 존재 여부를 확인한다.")
    void checkProductExists() {
        // given
        given(productRepository.existsById(1L)).willReturn(true);
        given(productRepository.existsById(2L)).willReturn(false);

        // when
        ProductExistsCheckResponse responseExists = productService.checkProductExists(1L);
        ProductExistsCheckResponse responseNotExists = productService.checkProductExists(2L);

        // then
        assertThat(responseExists.isExists()).isTrue();
        assertThat(responseNotExists.isExists()).isFalse();
    }

    @Test
    @DisplayName("상품을 추가한다 (재고 한정 상품 아님).")
    void addProduct_NoLimitedStock() {
        // given
        ProductAddRequest request = new ProductAddRequest(
                "New Game", 49.99, "New Desc", "new.jpg", "new.com", "New Dev",
                ProductState.ON_SALE, ProductDiscountPolicy.REGULAR, null, List.of("RPG")
        );
        Category mockCategory = mock(Category.class);

        given(productRepository.save(any(Product.class))).willReturn(Product.from(request));
        given(categoryRepository.findByName(anyString())).willReturn(Optional.empty());
        given(categoryRepository.save(any(Category.class))).willReturn(mockCategory);
        given(mockCategory.getId()).willReturn(1L);


        // when
        productService.addProduct(request);

        // then
        verify(productRepository, times(1)).save(any(Product.class));
        verify(categoryRepository, times(1)).save(any());
        verify(productCategoryRepository, times(1)).save(any());
    }
}

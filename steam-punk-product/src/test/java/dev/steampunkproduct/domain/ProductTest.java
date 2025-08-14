package dev.steampunkproduct.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dev.steampunkproduct.dto.request.ProductAddRequest;
import java.util.List;
import dev.steampunkproduct.enumtype.ProductState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Product 도메인 단위 테스트")
class ProductTest {

    @Test
    @DisplayName("ProductAddRequest로부터 Product 객체를 생성한다.")
    void from() {
        // given
        ProductAddRequest request = new ProductAddRequest(
                "Test Game",
                59.99,
                "A test game description.",
                "header.jpg",
                "http://testgame.com",
                "Test Developer",
                ProductState.ON_SALE,
                ProductDiscountPolicy.REGULAR,
                null,
                List.of()
        );

        // when
        Product product = Product.from(request);

        // then
        assertThat(product.getName()).isEqualTo(request.name());
        assertThat(product.getPrice()).isEqualTo(request.price());
        assertThat(product.getShortDescription()).isEqualTo(request.shortDescription());
        assertThat(product.getHeaderImage()).isEqualTo(request.headerImage());
        assertThat(product.getWebSite()).isEqualTo(request.webSite());
        assertThat(product.getDeveloper()).isEqualTo(request.developer());
        assertThat(product.getProductState()).isEqualTo(request.productState());
        assertThat(product.getProductDiscountPolicy()).isEqualTo(request.productDiscountPolicy());
    }
}

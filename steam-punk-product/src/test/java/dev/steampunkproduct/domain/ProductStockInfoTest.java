package dev.steampunkproduct.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductStockInfo 도메인 단위 테스트")
class ProductStockInfoTest {

    @Test
    @DisplayName("비제한 상품에 대한 ProductStockInfo 객체를 생성한다.")
    void ofNonLimitedProduct() {
        // when
        ProductStockInfo stockInfo = ProductStockInfo.ofNonLimitedProduct();

        // then
        assertThat(stockInfo).isNotNull();
        assertThat(stockInfo.stockQuantity()).isNull();
    }
}

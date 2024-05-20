package dev.steampunkproductstock.service;

import static org.assertj.core.api.Assertions.assertThat;

import dev.steampunkproductstock.domain.ProductStock;
import dev.steampunkproductstock.dto.request.ProductStockAddRequest;
import dev.steampunkproductstock.dto.response.ProductStockAddResponse;
import dev.steampunkproductstock.repository.ProductStockRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class StockServiceTest {

    @Autowired
    StockService stockService;

    @Autowired
    ProductStockRepository productStockRepository;

    @Transactional
    @Test
    void addProductStock() {
        ProductStockAddRequest request = new ProductStockAddRequest(1L, 10L);
        ProductStockAddResponse res = stockService.addProductStock(request);

        assertThat(res.stockQuantity()).isEqualTo(10L);
    }

    @Test
    void findProductStock() {
    }

    @Test
    @DisplayName(value = "상품재고 10개 1000명 동시접근")
    void updateProductStock() throws InterruptedException {
        ProductStockAddRequest request = new ProductStockAddRequest(1L, 10L);
        ProductStockAddResponse res = stockService.addProductStock(request);
        Long targetProductId = res.productId();

        int threadCnt = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCnt);
        for (int i = 0; i < threadCnt; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decreaseProductStock(targetProductId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        ProductStock productStock = productStockRepository.findById(1L).get();
        assertThat(productStock.getStockQuantity()).isEqualTo(0L);
    }
}
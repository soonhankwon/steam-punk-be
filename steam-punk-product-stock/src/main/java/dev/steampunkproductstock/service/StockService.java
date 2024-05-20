package dev.steampunkproductstock.service;

import dev.steampunkproductstock.common.enumtype.ErrorCode;
import dev.steampunkproductstock.common.exception.ApiException;
import dev.steampunkproductstock.domain.ProductStock;
import dev.steampunkproductstock.dto.request.ProductStockAddRequest;
import dev.steampunkproductstock.dto.response.ProductStockAddResponse;
import dev.steampunkproductstock.dto.response.ProductStockGetResponse;
import dev.steampunkproductstock.repository.ProductStockRepository;
import dev.steampunkproductstock.repository.StockQuantityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockService {

    private final ProductStockRepository productStockRepository;
    private final StockQuantityRepository stockQuantityRepository;
    private final StockTransactionService stockTransactionService;


    @Transactional
    public ProductStockAddResponse addProductStock(ProductStockAddRequest request) {
        ProductStock productStock = ProductStock.from(request);
        productStock = productStockRepository.save(productStock);
        stockQuantityRepository.putStockQuantity(
                productStock.getProductId(),
                productStock.getStockQuantity()
        );
        return ProductStockAddResponse.from(productStock);
    }

    @Transactional(readOnly = true)
    public ProductStockGetResponse findProductStock(Long productId) {
        ProductStock productStock = productStockRepository.findByProductId(productId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_PRODUCT_ID));

        return ProductStockGetResponse.from(productStock);
    }

    public ProductStockGetResponse decreaseProductStock(Long productId) {
        // Redis 싱글 스레드를 활용하여 재고수량 만큼 접근(eg 1000 -> 10)
        Long stockCount = stockQuantityRepository.decrement(productId);
        log.info("stockCnt={}", stockCount);
        if (!hasStock(stockCount)) {
            throw new ApiException(ErrorCode.NO_STOCK_BY_PRODUCT_ID);
        }
        // 재고 수량만큼의 스레드만 트랜잭션 시작(eg 10) -> 이후 재고수량 만큼의 스레드의 동시성 제어는 DB 비관적락 적용(쓰기 락)
        ProductStock productStock = stockTransactionService.decreaseByTransaction(productId);
        return ProductStockGetResponse.from(productStock);
    }

    private static boolean hasStock(Long stockCount) {
        return stockCount + 1 > 0;
    }
}

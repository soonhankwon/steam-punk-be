package dev.steampunkproductstock.service;

import dev.steampunkproductstock.common.enumtype.ErrorCode;
import dev.steampunkproductstock.common.exception.ApiException;
import dev.steampunkproductstock.domain.ProductStock;
import dev.steampunkproductstock.repository.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockTransactionService {

    private final ProductStockRepository productStockRepository;

    @Transactional
    public ProductStock decreaseByTransaction(Long productId) {
        // 비관적락 적용 - 쓰기 락
        ProductStock productStock = productStockRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_PRODUCT_ID));
        productStock.decreaseStock();
        log.info("realStock={}", productStock.getStockQuantity());
        return productStock;
    }
}

package dev.steampunkproductstock.service;

import dev.steampunkproductstock.common.enumtype.ErrorCode;
import dev.steampunkproductstock.common.exception.ApiException;
import dev.steampunkproductstock.domain.ProductStock;
import dev.steampunkproductstock.dto.request.ProductStockAddRequest;
import dev.steampunkproductstock.dto.response.ProductStockAddResponse;
import dev.steampunkproductstock.dto.response.ProductStockGetResponse;
import dev.steampunkproductstock.repository.ProductStockRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockService {

    private final ProductStockRepository productStockRepository;
    private final StockTransactionService stockTransactionService;
    private final RedissonClient redissonClient;


    @Transactional
    public ProductStockAddResponse addProductStock(ProductStockAddRequest request) {
        ProductStock productStock = ProductStock.from(request);
        productStock = productStockRepository.save(productStock);
        return ProductStockAddResponse.from(productStock);
    }

    @Transactional(readOnly = true)
    public ProductStockGetResponse findProductStock(Long productId) {
        ProductStock productStock = productStockRepository.findByProductId(productId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_PRODUCT_ID));

        return ProductStockGetResponse.from(productStock);
    }

    public ProductStockGetResponse decreaseProductStock(Long productId) {
        // Redisson 분산락 적용(Pub,Sub)
        RLock lock = redissonClient.getLock(String.valueOf(productId));
        ProductStock productStock;
        try {
            // waitTime: 락을 기다리는 시간, leaseTime: 락 임대 시간
            lock.tryLock(5, 3, TimeUnit.SECONDS);
            // Lock을 획득한 요청만 트랜잭션 시작
            productStock = stockTransactionService.decreaseByTransaction(productId);
        } catch (InterruptedException e) {
            throw new ApiException(ErrorCode.NO_STOCK_BY_PRODUCT_ID);
        } finally {
            lock.unlock();
        }
        return ProductStockGetResponse.from(productStock);
    }
}

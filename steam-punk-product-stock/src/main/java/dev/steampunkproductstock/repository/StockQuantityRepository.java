package dev.steampunkproductstock.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StockQuantityRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void putStockQuantity(Long productId, Long quantity) {
        redisTemplate
                .opsForValue()
                .set("stock_quantity::" + productId, String.valueOf(quantity));
    }

    public Long decrement(Long productId) {
        return redisTemplate
                .opsForValue()
                .decrement("stock_quantity::" + productId);
    }
}

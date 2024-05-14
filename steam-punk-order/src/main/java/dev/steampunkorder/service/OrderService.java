package dev.steampunkorder.service;

import dev.steampunkorder.common.enumtype.ErrorCode;
import dev.steampunkorder.common.exception.ApiException;
import dev.steampunkorder.domain.Order;
import dev.steampunkorder.domain.OrderProduct;
import dev.steampunkorder.domain.ProductInfo;
import dev.steampunkorder.dto.request.OrderAddRequest;
import dev.steampunkorder.dto.response.OrderAddResponse;
import dev.steampunkorder.dto.response.OrderGetResponse;
import dev.steampunkorder.repository.OrderProductRepository;
import dev.steampunkorder.repository.OrderRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

    @Transactional
    public OrderAddResponse addOrder(Long userId, OrderAddRequest request) {
        Order order = Order.ofPendingOrder(userId);
        order = orderRepository.save(order);

        //TODO 이전 게임(ProductId) 구매 이력이 있는지 체크로직 필요

        List<OrderProduct> orderProducts = new ArrayList<>();
        Order finalOrder = order;
        // productId가 중복되어서 주문되면 안됨
        request.productIds()
                .stream()
                .distinct()
                .forEach(productId -> {
                    orderProducts.add(new OrderProduct(finalOrder.getId(), productId));
                });

        orderProductRepository.saveAll(orderProducts);
        return OrderAddResponse.from(order);
    }

    @Transactional(readOnly = true)
    public OrderGetResponse findOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_ORDER_ID));

        AtomicReference<Long> totalPrice = new AtomicReference<>(0L);
        orderProductRepository.findAllByOrderId(orderId)
                .forEach(orderProduct -> {
                    ProductInfo productInfo = getProductInfo(orderProduct.getProductId());
                    totalPrice.updateAndGet(v -> v + productInfo.price());
                });

        return OrderGetResponse.of(order, totalPrice.get());
    }

    private ProductInfo getProductInfo(Long productId) {
        return WebClient.create()
                .get()
                .uri("http://localhost:8080/api/v1/products/" + productId)
                .retrieve()
                .bodyToMono(ProductInfo.class)
                .block();
    }
}

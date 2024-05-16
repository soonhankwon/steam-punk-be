package dev.steampunkorder.service;

import dev.steampunkorder.common.enumtype.ErrorCode;
import dev.steampunkorder.common.exception.ApiException;
import dev.steampunkorder.domain.Order;
import dev.steampunkorder.domain.OrderProduct;
import dev.steampunkorder.domain.ProductInfo;
import dev.steampunkorder.dto.request.OrderAddRequest;
import dev.steampunkorder.dto.request.OrderUpdateRequest;
import dev.steampunkorder.dto.response.OrderAddResponse;
import dev.steampunkorder.dto.response.OrderGetResponse;
import dev.steampunkorder.dto.response.OrderUpdateResponse;
import dev.steampunkorder.enumtype.OrderState;
import dev.steampunkorder.repository.OrderProductRepository;
import dev.steampunkorder.repository.OrderRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

        // 이미 구매한 이력이 있는 상품을 재주문할 수 없음
        //TODO order & orderProduct 테이블을 조인해서 이미 구매한 상품 ID 리스트를 바로 조회하도록 쿼리 개선 필요
        List<Order> paidOrders = orderRepository.findByUserIdAndOrderState(userId,
                OrderState.ORDER_PAYMENT_COMPLETED);

        Set<Long> paidProductIdsSet = new HashSet<>();
        paidOrders.forEach(o -> {
            List<OrderProduct> paidOrderProducts = orderProductRepository.findAllByOrderId(o.getId());
            paidOrderProducts.stream()
                    .map(OrderProduct::getProductId)
                    .forEach(paidProductIdsSet::add);
        });

        List<OrderProduct> orderProducts = new ArrayList<>();
        Order finalOrder = order;
        // productId가 중복되어서 주문되면 안됨
        request.productIds()
                .stream()
                .distinct()
                .forEach(productId -> {
                    if (paidProductIdsSet.contains(productId)) {
                        throw new ApiException(ErrorCode.EXISTS_PAID_HISTORY_PRODUCT);
                    }
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
        List<Long> orderProductIds = new ArrayList<>();
        orderProductRepository.findAllByOrderId(orderId)
                .forEach(orderProduct -> {
                    ProductInfo productInfo = getProductInfo(orderProduct.getProductId());
                    orderProductIds.add(orderProduct.getProductId());
                    totalPrice.updateAndGet(v -> v + productInfo.price());
                });

        return OrderGetResponse.of(order, totalPrice.get(), orderProductIds);
    }

    @Transactional
    public OrderUpdateResponse updateOrder(Long orderId, OrderUpdateRequest request) {
        Order order = orderRepository.findByIdAndUserId(orderId, request.userId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_ORDER_ID_BY_USER));
        order.updateState(request.orderState());
        return OrderUpdateResponse.from(order);
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

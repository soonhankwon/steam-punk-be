package dev.steampunkorder.service;

import dev.steampunkorder.common.enumtype.ErrorCode;
import dev.steampunkorder.common.exception.ApiException;
import dev.steampunkorder.domain.Order;
import dev.steampunkorder.domain.OrderProduct;
import dev.steampunkorder.domain.ProductInfo;
import dev.steampunkorder.domain.WishList;
import dev.steampunkorder.dto.request.OrderProductDeleteRequest;
import dev.steampunkorder.dto.request.OrderUpdateRequest;
import dev.steampunkorder.dto.response.OrderAddResponse;
import dev.steampunkorder.dto.response.OrderGetResponse;
import dev.steampunkorder.dto.response.OrderGetResponse.OrderProductDTO;
import dev.steampunkorder.dto.response.OrderProductDeleteResponse;
import dev.steampunkorder.dto.response.OrderUpdateResponse;
import dev.steampunkorder.enumtype.OrderProductState;
import dev.steampunkorder.repository.OrderProductRepository;
import dev.steampunkorder.repository.OrderRepository;
import dev.steampunkorder.repository.WishListRepository;
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
    private final WishListRepository wishListRepository;

    @Transactional
    public OrderAddResponse addOrder(Long userId) {
        Order order = Order.ofPendingOrder(userId);
        final Order finalOrder = orderRepository.save(order);
        /*
         * 이미 구매한 이력이 있는 상품을 재주문할 수 없음 -> 위시리스트에 담아논 상품만 주문이 가능함
         * 위시리스트에 상품을 담을 때 구매 이력이 있으면 예외처리됨
         */
        List<WishList> wishLists = wishListRepository.findAllByUserId(userId)
                .stream()
                .toList();

        // productId가 중복되어서 주문되면 안됨
        List<OrderProduct> orderProducts = new ArrayList<>();
        wishLists.stream()
                .map(WishList::getProductId)
                .distinct()
                .forEach(productId -> {
                    ProductInfo productInfo = getProductInfo(productId);
                    // 주문한 상품의 할인 및 한정 판매 상태
                    OrderProductState orderProductState = productInfo.productState();
                    orderProducts.add(OrderProduct.of(
                            finalOrder.getId(),
                            productId,
                            productInfo.price(),
                            orderProductState)
                    );
                });
        // 주문상품 목록 INSERT
        orderProductRepository.saveAll(orderProducts);
        // 주문성공 후 위시리스트에서는 삭제
        wishListRepository.deleteAll(wishLists);
        return OrderAddResponse.from(order);
    }

    @Transactional(readOnly = true)
    public OrderGetResponse findOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_ORDER_ID));

        AtomicReference<Long> totalPrice = new AtomicReference<>(0L);
        List<OrderProductDTO> orderProductDTOS = new ArrayList<>();
        orderProductRepository.findAllByOrderId(orderId)
                .forEach(orderProduct -> {
                    Long productId = orderProduct.getProductId();
                    ProductInfo productInfo = getProductInfo(productId);
                    orderProductDTOS.add(OrderProductDTO.of(productId, productInfo));
                    totalPrice.updateAndGet(v -> v + productInfo.price());
                });

        return OrderGetResponse.of(order, totalPrice.get(), orderProductDTOS);
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
                .uri("http://localhost:8080/api/v1/products/{productId}", productId)
                .retrieve()
                .bodyToMono(ProductInfo.class)
                .block();
    }

    @Transactional
    public OrderProductDeleteResponse deleteOrderProduct(Long orderProductId, OrderProductDeleteRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_ORDER_ID));

        order.validateUser(request.userId());
        OrderProduct orderProduct = orderProductRepository.findById(orderProductId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_ORDER_PRODUCT_ID));
        orderProductRepository.delete(orderProduct);
        return OrderProductDeleteResponse.ofSuccess();
    }
}

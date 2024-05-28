package dev.steampunkorder.service;

import dev.steampunkorder.common.enumtype.ErrorCode;
import dev.steampunkorder.common.exception.ApiException;
import dev.steampunkorder.domain.OrderProduct;
import dev.steampunkorder.domain.WishList;
import dev.steampunkorder.dto.WishListDTO;
import dev.steampunkorder.dto.request.WishListAddRequest;
import dev.steampunkorder.dto.request.WishListDeleteRequest;
import dev.steampunkorder.dto.response.ProductsExistsCheckResponse;
import dev.steampunkorder.dto.response.WishListAddResponse;
import dev.steampunkorder.dto.response.WishListDeleteResponse;
import dev.steampunkorder.dto.response.WishListGetResponse;
import dev.steampunkorder.enumtype.OrderState;
import dev.steampunkorder.repository.OrderProductRepository;
import dev.steampunkorder.repository.OrderRepository;
import dev.steampunkorder.repository.WishListRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Service
public class WishListService {

    private final WishListRepository wishListRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

    @Transactional
    public WishListAddResponse addWishList(WishListAddRequest request) {
        Long userId = request.userId();
        // 동일한 상품을 위시리스트에 담을 수 없음
        if (wishListRepository.existsByUserIdAndProductId(userId, request.productId())) {
            throw new ApiException(ErrorCode.EXISTS_PRODUCT_ID_IN_WISHLIST);
        }
        // 위시리스트에 이미 결제완료된 주문의 상품을 담을 수 없음 (예외처리)
        Long productId = request.productId();
        boolean isExistPaidProduct = orderRepository.findByUserIdAndOrderState(userId, OrderState.ORDER_PAID)
                .stream()
                .flatMap(o -> orderProductRepository.findAllByOrderId(o.getId()).stream()
                )
                .map(OrderProduct::getProductId)
                .anyMatch(id -> id.equals(productId));
        if (isExistPaidProduct) {
            throw new ApiException(ErrorCode.EXISTS_PAID_HISTORY_PRODUCT);
        }

        ProductsExistsCheckResponse productsExistsCheckResponse = checkProductExists(request);
        if (!productsExistsCheckResponse.isExists()) {
            throw new ApiException(ErrorCode.NOT_EXISTS_PRODUCT_ID);
        }
        WishList wishList = WishList.from(request);
        wishList = wishListRepository.save(wishList);
        return WishListAddResponse.from(wishList);
    }

    private ProductsExistsCheckResponse checkProductExists(WishListAddRequest request) {
        return WebClient.create()
                .get()
                .uri("http://localhost:8080/api/v1/products/" + request.productId() + "/check")
                .retrieve()
                .bodyToMono(ProductsExistsCheckResponse.class)
                .block();
    }

    @Transactional(readOnly = true)
    public WishListGetResponse getWishList(Long userId) {
        List<WishListDTO> wishListDTOS = wishListRepository.findAllByUserId(userId)
                .stream()
                .map(WishListDTO::from)
                .toList();

        return WishListGetResponse.of(userId, wishListDTOS);
    }

    @Transactional
    public WishListDeleteResponse deleteWishList(WishListDeleteRequest request) {
        WishList wishList = wishListRepository.findByIdAndUserId(request.wishListId(), request.userId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_PRODUCT_ID_BY_USER));
        wishListRepository.delete(wishList);
        return WishListDeleteResponse.ofSuccess();
    }
}

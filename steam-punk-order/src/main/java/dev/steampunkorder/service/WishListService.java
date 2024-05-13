package dev.steampunkorder.service;

import dev.steampunkorder.common.enumtype.ErrorCode;
import dev.steampunkorder.common.exception.ApiException;
import dev.steampunkorder.domain.WishList;
import dev.steampunkorder.dto.request.WishListAddRequest;
import dev.steampunkorder.dto.request.WishListDeleteRequest;
import dev.steampunkorder.dto.response.ProductsExistsCheckResponse;
import dev.steampunkorder.dto.response.WishListAddResponse;
import dev.steampunkorder.dto.response.WishListDeleteResponse;
import dev.steampunkorder.dto.response.WishListGetResponse;
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

    @Transactional
    public WishListAddResponse addWishList(WishListAddRequest request) {
        if (wishListRepository.existsByUserIdAndProductId(request.userId(), request.productId())) {
            throw new ApiException(ErrorCode.EXISTS_PRODUCT_ID_IN_WISHLIST);
        }
        ProductsExistsCheckResponse productsExistsCheckResponse = checkProductExists(request);
        if (!productsExistsCheckResponse.isExists()) {
            throw new ApiException(ErrorCode.NOT_EXIST_PRODUCT_ID);
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
        List<Long> productIds = wishListRepository.findAllByUserId(userId)
                .stream()
                .map(WishList::getProductId)
                .toList();

        return WishListGetResponse.of(userId, productIds);
    }

    @Transactional
    public WishListDeleteResponse deleteWishList(WishListDeleteRequest request) {
        WishList wishList = wishListRepository.findByUserIdAndProductId(request.userId(), request.productId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_PRODUCT_ID_BY_USER));
        wishListRepository.delete(wishList);
        return WishListDeleteResponse.ofSuccess();
    }
}

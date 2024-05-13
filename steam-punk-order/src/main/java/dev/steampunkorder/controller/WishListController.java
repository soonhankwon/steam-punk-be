package dev.steampunkorder.controller;

import dev.steampunkorder.dto.request.WishListAddRequest;
import dev.steampunkorder.dto.request.WishListDeleteRequest;
import dev.steampunkorder.dto.response.WishListAddResponse;
import dev.steampunkorder.dto.response.WishListDeleteResponse;
import dev.steampunkorder.dto.response.WishListGetResponse;
import dev.steampunkorder.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/wishlists")
public class WishListController {

    private final WishListService wishListService;

    @PostMapping
    public ResponseEntity<WishListAddResponse> addWishList(@RequestBody WishListAddRequest request) {
        WishListAddResponse res = wishListService.addWishList(request);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<WishListGetResponse> getWishList(@PathVariable Long userId) {
        WishListGetResponse res = wishListService.getWishList(userId);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping
    public ResponseEntity<WishListDeleteResponse> deleteWishList(@RequestBody WishListDeleteRequest request) {
        WishListDeleteResponse res = wishListService.deleteWishList(request);
        return ResponseEntity.ok(res);
    }
}

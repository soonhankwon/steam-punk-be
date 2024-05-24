package dev.steampunkorder.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkorder.dto.WishListDTO;
import java.util.List;

public record WishListGetResponse(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("documents")
        List<WishListDTO> wishListDTOS
) {
    public static WishListGetResponse of(Long userId, List<WishListDTO> wishListDTOS) {
        return new WishListGetResponse(
                userId,
                wishListDTOS
        );
    }
}

package dev.steampunkorder.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderProductDeleteResponse(
        @JsonProperty("is_deleted")
        Boolean isDeleted
) {
    public static OrderProductDeleteResponse ofSuccess() {
        return new OrderProductDeleteResponse(Boolean.TRUE);
    }
}

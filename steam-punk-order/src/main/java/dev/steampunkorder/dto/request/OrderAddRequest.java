package dev.steampunkorder.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record OrderAddRequest(
        @JsonProperty("product_ids")
        List<Long> productIds
) {
}

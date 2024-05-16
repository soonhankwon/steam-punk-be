package dev.steampunkpayment.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record UserGamePlayHistoryInfo(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("product_id")
        Long productId,
        @JsonProperty("game_state")
        String gameState,
        @JsonProperty("created_at")
        LocalDateTime createdAt,
        @JsonProperty("played_at")
        LocalDateTime playedAt
) {
    public boolean hasRefundCondition(RefundPolicy refundPolicy) {
        return refundPolicy.hasRefundCondition(this.gameState, this.createdAt, this.playedAt);
    }
}

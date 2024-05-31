package dev.steampunkpayment.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import org.springframework.web.reactive.function.client.WebClient;

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

    public static UserGamePlayHistoryInfo ofUserGamePlayHistoryInternalApi(Long userId, Long productId) {
        return WebClient.create()
                .get()
                .uri("http://localhost:8080/api/v1/games/{userId}?productId={productId}",
                        userId, productId)
                .retrieve()
                .bodyToMono(UserGamePlayHistoryInfo.class)
                .block();
    }
}

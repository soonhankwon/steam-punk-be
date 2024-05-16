package dev.steampunkgame.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkgame.domain.UserGameHistory;
import dev.steampunkgame.enumtype.GameState;
import java.time.LocalDateTime;

public record UserGameHistoryGetResponse(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("product_id")
        Long productId,
        @JsonProperty("game_state")
        GameState gameState,
        @JsonProperty("created_at")
        LocalDateTime createdAt,
        @JsonProperty("played_at")
        LocalDateTime playedAt
) {
    public static UserGameHistoryGetResponse from(UserGameHistory userGameHistory) {
        LocalDateTime playedAt = null;
        if (userGameHistory.getGameState() == GameState.PLAYED) {
            playedAt = userGameHistory.getModifiedAt();
        }
        return new UserGameHistoryGetResponse(
                userGameHistory.getUserId(),
                userGameHistory.getProductId(),
                userGameHistory.getGameState(),
                userGameHistory.getCreatedAt(),
                playedAt
        );
    }
}

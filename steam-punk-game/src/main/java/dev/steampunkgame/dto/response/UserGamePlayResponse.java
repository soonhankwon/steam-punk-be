package dev.steampunkgame.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkgame.domain.UserGameHistory;
import dev.steampunkgame.enumtype.GameState;

public record UserGamePlayResponse(
        @JsonProperty("game_id")
        Long gameId,
        @JsonProperty("game_state")
        GameState gameState
) {
    public static UserGamePlayResponse from(UserGameHistory userGameHistory) {
        return new UserGamePlayResponse(
                userGameHistory.getProductId(),
                userGameHistory.getGameState()
        );
    }
}

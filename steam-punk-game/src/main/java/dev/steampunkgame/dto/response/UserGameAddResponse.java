package dev.steampunkgame.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkgame.domain.UserGameHistory;
import dev.steampunkgame.enumtype.GameState;

public record UserGameAddResponse(
        @JsonProperty("game_id")
        Long gameId,
        @JsonProperty("game_state")
        GameState gameState
) {

    public static UserGameAddResponse from(UserGameHistory userGameHistory) {
        return new UserGameAddResponse(
                userGameHistory.getId(),
                userGameHistory.getGameState()
        );
    }
}

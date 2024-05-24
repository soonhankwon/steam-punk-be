package dev.steampunkgame.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkgame.domain.UserGameHistory;
import dev.steampunkgame.enumtype.GameState;

public record GameDTO(
        @JsonProperty("game_id")
        Long gameId,
        @JsonProperty("game_state")
        GameState gameState
) {
    public static GameDTO from(UserGameHistory userGameHistory) {
        return new GameDTO(
                userGameHistory.getId(),
                userGameHistory.getGameState()
        );
    }
}

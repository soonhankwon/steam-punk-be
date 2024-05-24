package dev.steampunkgame.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkgame.dto.GameDTO;
import java.util.List;

public record UserGameAddResponse(
        Long userId,
        @JsonProperty("document")
        List<GameDTO> gameDTOS
) {

    public static UserGameAddResponse of(Long userId, List<GameDTO> gameDTOS) {
        return new UserGameAddResponse(
                userId,
                gameDTOS
        );
    }
}

package dev.steampunkgame.domain;

import dev.steampunkgame.common.entity.BaseTimeEntity;
import dev.steampunkgame.dto.request.UserGameAddRequest;
import dev.steampunkgame.enumtype.GameState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_game_history")
public class UserGameHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_game_history_id")
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    private GameState gameState;

    private UserGameHistory(Long productId, Long userId, GameState gameState) {
        this.productId = productId;
        this.userId = userId;
        this.gameState = gameState;
    }

    public static UserGameHistory ofNotPlayedState(Long productId, UserGameAddRequest request) {
        return new UserGameHistory(
                productId,
                request.userId(),
                GameState.NOT_PLAYED
        );
    }

    public void played() {
        if (this.gameState == GameState.PLAYED) {
            return;
        }
        this.gameState = GameState.PLAYED;
    }
}

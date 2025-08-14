package dev.steampunkpayment.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RegularRefundPolicy 도메인 단위 테스트")
class RegularRefundPolicyTest {

    private final RegularRefundPolicy refundPolicy = new RegularRefundPolicy();

    @Test
    @DisplayName("플레이하지 않았고, 구매 후 7일 이내일 경우 환불 가능하다.")
    void hasRefundCondition_Refundable() {
        // given
        String gameState = "NOT_PLAYED";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(3);
        LocalDateTime playedAt = null;

        // when
        boolean result = refundPolicy.hasRefundCondition(gameState, createdAt, playedAt);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("플레이하지 않았지만, 구매 후 7일이 지났을 경우 환불 불가능하다.")
    void hasRefundCondition_NotRefundable_TooOld() {
        // given
        String gameState = "NOT_PLAYED";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(8);
        LocalDateTime playedAt = null;

        // when
        boolean result = refundPolicy.hasRefundCondition(gameState, createdAt, playedAt);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("이미 플레이 했을 경우 환불 불가능하다.")
    void hasRefundCondition_NotRefundable_AlreadyPlayed() {
        // given
        String gameState = "PLAYED";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime playedAt = LocalDateTime.now();

        // when
        boolean result = refundPolicy.hasRefundCondition(gameState, createdAt, playedAt);

        // then
        assertThat(result).isFalse();
    }
}

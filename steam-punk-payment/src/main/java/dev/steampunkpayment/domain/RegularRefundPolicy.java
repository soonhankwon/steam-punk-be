package dev.steampunkpayment.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class RegularRefundPolicy implements RefundPolicy {

    @Override
    public boolean hasRefundCondition(String gameState, LocalDateTime createdAt, LocalDateTime playedAt) {
        // NOT_PLAYED인 경우 구매후 7일이내인 경우 환불 가능
        if (gameState.equals("NOT_PLAYED")) {
            LocalDateTime now = LocalDateTime.now();
            long daysBetween = ChronoUnit.DAYS.between(createdAt, now);
            return daysBetween < 8;
        }
        // 이외는 모두 환불 불가
        return false;
    }
}

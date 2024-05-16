package dev.steampunkpayment.domain;

import java.time.LocalDateTime;

public interface RefundPolicy {
    boolean hasRefundCondition(String gameState, LocalDateTime createdAtt, LocalDateTime playedAt);
}

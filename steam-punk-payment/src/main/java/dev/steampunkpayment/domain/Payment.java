package dev.steampunkpayment.domain;

import dev.steampunkpayment.common.entity.BaseTimeEntity;
import dev.steampunkpayment.dto.request.PaymentAddRequest;
import dev.steampunkpayment.enumtype.PaymentState;
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
@Table(name = "payment")
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "total_price")
    private Long totalPrice;

    @Enumerated(EnumType.STRING)
    private PaymentState paymentState;

    private Payment(Long userId, Long orderId, Long totalPrice) {
        this.userId = userId;
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.paymentState = PaymentState.PAYMENT_READY;
    }

    public static Payment ofReady(PaymentAddRequest request, OrderInfo orderInfo) {
        return new Payment(
                request.userId(),
                request.orderId(),
                orderInfo.orderMetaData().totalPrice()
        );
    }

    public void refundInProgress(boolean isPartialRefund) {
        if (isPartialRefund) {
            this.paymentState = PaymentState.PAYMENT_PARTIAL_REFUND_IN_PROGRESS;
            return;
        }
        this.paymentState = PaymentState.PAYMENT_REFUND_IN_PROGRESS;
    }

    public void complete() {
        if (this.paymentState == PaymentState.PAYMENT_COMPLETED) {
            return;
        }
        this.paymentState = PaymentState.PAYMENT_COMPLETED;
    }
}

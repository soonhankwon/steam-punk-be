package dev.steampunkpayment.domain;

import dev.steampunkpayment.common.entity.BaseTimeEntity;
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
@Table(name = "payment_product")
public class PaymentProduct extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_product_id")
    private Long id;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "price")
    private Long price;

    @Enumerated(EnumType.STRING)
    private PaymentState paymentState;

    public PaymentProduct(Long paymentId, Long productId, Long price, PaymentState paymentState) {
        this.paymentId = paymentId;
        this.productId = productId;
        this.price = price;
        this.paymentState = paymentState;
    }

    public static PaymentProduct of(Long paymentId, Long productId, Long productPrice, PaymentState paymentState) {
        return new PaymentProduct(
                paymentId,
                productId,
                productPrice,
                paymentState
        );
    }

    public void paid() {
        if (this.paymentState == PaymentState.PAYMENT_COMPLETED) {
            return;
        }
        this.paymentState = PaymentState.PAYMENT_COMPLETED;
    }

    public void refundInProgress() {
        if (this.paymentState == PaymentState.PAYMENT_REFUND_IN_PROGRESS) {
            return;
        }
        this.paymentState = PaymentState.PAYMENT_REFUND_IN_PROGRESS;
    }
}

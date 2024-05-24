package dev.steampunkpayment.domain;

import dev.steampunkpayment.common.entity.BaseTimeEntity;
import dev.steampunkpayment.enumtype.PaymentProductState;
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
    private PaymentProductState paymentProductState;

    public PaymentProduct(Long paymentId, Long productId, Long price, PaymentProductState paymentProductState) {
        this.paymentId = paymentId;
        this.productId = productId;
        this.price = price;
        this.paymentProductState = paymentProductState;
    }

    public static PaymentProduct of(Long paymentId, Long productId, Long productPrice,
                                    PaymentProductState paymentProductState) {
        return new PaymentProduct(
                paymentId,
                productId,
                productPrice,
                paymentProductState
        );
    }

    public void paid() {
        this.paymentProductState = PaymentProductState.PAID;
    }

    public void refundInProgress() {
        this.paymentProductState = PaymentProductState.REFUND_IN_PROGRESS;
    }
}

package dev.steampunkpayment.domain;

import dev.steampunkpayment.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "payment_refund_product")
public class PaymentRefundProduct extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_refund_product_id")
    private Long id;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "price")
    private Long price;

    private PaymentRefundProduct(Long paymentId, Long productId, Long price) {
        this.paymentId = paymentId;
        this.productId = productId;
        this.price = price;
    }

    public static PaymentRefundProduct of(Long paymentId, UserGamePlayHistoryInfo userGamePlayHistoryInfo, Long price) {
        return new PaymentRefundProduct(
                paymentId,
                userGamePlayHistoryInfo.productId(),
                price
        );
    }
}

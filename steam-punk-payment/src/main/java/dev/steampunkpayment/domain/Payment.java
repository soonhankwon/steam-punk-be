package dev.steampunkpayment.domain;

import dev.steampunkpayment.common.entity.BaseTimeEntity;
import dev.steampunkpayment.dto.request.PaymentAddRequest;
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

    private Payment(Long userId, Long orderId, Long totalPrice) {
        this.userId = userId;
        this.orderId = orderId;
        this.totalPrice = totalPrice;
    }

    public static Payment of(PaymentAddRequest request, OrderInfo orderInfo) {
        return new Payment(
                request.userId(),
                request.orderId(),
                orderInfo.totalPrice()
        );
    }
}
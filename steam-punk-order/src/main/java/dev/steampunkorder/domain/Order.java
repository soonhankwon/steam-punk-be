package dev.steampunkorder.domain;

import dev.steampunkorder.common.entity.BaseTimeEntity;
import dev.steampunkorder.enumtype.OrderState;
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
@Table(name = "`order`")
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    private OrderState orderState;

    private Order(Long userId, OrderState orderState) {
        this.userId = userId;
        this.orderState = orderState;
    }

    public static Order ofPendingOrder(Long userId) {
        return new Order(
                userId,
                OrderState.ORDER_PENDING
        );
    }

    public void updateState(OrderState orderState) {
        if (this.orderState == orderState) {
            return;
        }
        this.orderState = orderState;
    }
}

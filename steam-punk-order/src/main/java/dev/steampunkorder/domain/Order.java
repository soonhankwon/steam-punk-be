package dev.steampunkorder.domain;

import dev.steampunkorder.common.entity.BaseTimeEntity;
import dev.steampunkorder.common.enumtype.ErrorCode;
import dev.steampunkorder.common.exception.ApiException;
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
                OrderState.READY
        );
    }

    public void updateState(OrderState orderState) {
        if (this.orderState == OrderState.ORDER_PAID) {
            throw new ApiException(ErrorCode.CANT_UPDATE_STATE_OF_PAID_PRODUCT);
        }
        this.orderState = orderState;
    }

    public void validateUser(Long userId) {
        if (!this.userId.equals(userId)) {
            throw new ApiException(ErrorCode.NOT_USER_ORDER);
        }
    }
}

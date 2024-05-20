package dev.steampunkorder.domain;


import dev.steampunkorder.common.entity.BaseTimeEntity;
import dev.steampunkorder.enumtype.OrderProductState;
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
@Table(name = "order_product")
public class OrderProduct extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_product_id")
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "price")
    private Long price;

    // 주문한 상품의 할인 및 한정 판매 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "order_product_state")
    private OrderProductState orderProductState;

    private OrderProduct(Long orderId, Long productId, Long price, OrderProductState orderProductState) {
        this.orderId = orderId;
        this.productId = productId;
        this.price = price;
        this.orderProductState = orderProductState;
    }

    public static OrderProduct of(Long orderId, Long productId, Long price, OrderProductState orderProductState) {
        return new OrderProduct(
                orderId,
                productId,
                price,
                orderProductState
        );
    }
}

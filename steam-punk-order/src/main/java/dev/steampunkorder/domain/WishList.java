package dev.steampunkorder.domain;

import dev.steampunkorder.common.entity.BaseTimeEntity;
import dev.steampunkorder.dto.request.WishListAddRequest;
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
@Table(name = "wish_list")
public class WishList extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wish_list_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "product_id")
    private Long productId;

    private WishList(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public static WishList from(WishListAddRequest request) {
        return new WishList(
                request.userId(),
                request.productId()
        );
    }
}

package dev.steampunkorder.repository;

import dev.steampunkorder.domain.Order;
import dev.steampunkorder.enumtype.OrderState;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    List<Order> findByUserIdAndOrderState(Long userId, OrderState orderState);
}

package dev.steampunkpayment.repository;

import dev.steampunkpayment.domain.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByOrderId(Long orderId);

    Page<Payment> findAllByUserId(Long userId, PageRequest pageRequest);
}

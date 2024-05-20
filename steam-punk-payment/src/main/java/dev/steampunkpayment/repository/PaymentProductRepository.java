package dev.steampunkpayment.repository;

import dev.steampunkpayment.domain.PaymentProduct;
import dev.steampunkpayment.enumtype.PaymentState;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentProductRepository extends JpaRepository<PaymentProduct, Long> {
    List<PaymentProduct> findAllByPaymentIdAndPaymentState(Long id, PaymentState paymentState);

    Optional<PaymentProduct> findByPaymentIdAndProductId(Long paymentId, Long productId);
}

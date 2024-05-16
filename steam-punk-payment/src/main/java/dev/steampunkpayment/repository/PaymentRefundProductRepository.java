package dev.steampunkpayment.repository;

import dev.steampunkpayment.domain.PaymentRefundProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRefundProductRepository extends JpaRepository<PaymentRefundProduct, Long> {
    List<PaymentRefundProduct> findAllByPaymentId(Long paymentId);
}

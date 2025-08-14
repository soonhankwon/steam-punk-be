package dev.steampunkpayment.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dev.steampunkpayment.dto.request.PaymentAddRequest;
import dev.steampunkpayment.enumtype.PaymentState;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Payment 도메인 단위 테스트")
class PaymentTest {

    private PaymentAddRequest request;
    private OrderInfo orderInfo;

    @BeforeEach
    void setUp() {
        Long userId = 1L;
        Long orderId = 100L;
        Long totalPrice = 50000L;
        request = new PaymentAddRequest(userId, orderId);
        OrderInfo.OrderMetaData metaData = new OrderInfo.OrderMetaData(orderId, userId, totalPrice);
        orderInfo = new OrderInfo(metaData, List.of());
    }

    @Test
    @DisplayName("요청과 주문 정보를 바탕으로 Payment 객체를 '준비' 상태로 생성한다.")
    void ofReady() {
        // when
        Payment payment = Payment.ofReady(request, orderInfo);

        // then
        assertThat(payment.getUserId()).isEqualTo(request.userId());
        assertThat(payment.getOrderId()).isEqualTo(request.orderId());
        assertThat(payment.getTotalPrice()).isEqualTo(orderInfo.orderMetaData().totalPrice());
        assertThat(payment.getPaymentState()).isEqualTo(PaymentState.READY);
    }

    @Test
    @DisplayName("결제 상태를 '완료'로 변경한다.")
    void complete() {
        // given
        Payment payment = Payment.ofReady(request, orderInfo);

        // when
        payment.complete();

        // then
        assertThat(payment.getPaymentState()).isEqualTo(PaymentState.PAID);
    }

    @Test
    @DisplayName("이미 '완료' 상태일 경우 상태가 변경되지 않는다.")
    void completeWhenAlreadyPaid() {
        // given
        Payment payment = Payment.ofReady(request, orderInfo);
        payment.complete(); // State is now PAID

        // when
        payment.complete(); // Call again

        // then
        assertThat(payment.getPaymentState()).isEqualTo(PaymentState.PAID);
    }

    @Nested
    @DisplayName("환불 진행 상태 변경 테스트")
    class RefundInProgressTest {

        @Test
        @DisplayName("부분 환불일 경우 '부분 환불 진행중' 상태로 변경한다.")
        void partialRefundInProgress() {
            // given
            Payment payment = Payment.ofReady(request, orderInfo);
            boolean isPartialRefund = true;

            // when
            payment.refundInProgress(isPartialRefund);

            // then
            assertThat(payment.getPaymentState()).isEqualTo(PaymentState.PARTIAL_REFUND_IN_PROGRESS);
        }

        @Test
        @DisplayName("전체 환불일 경우 '환불 진행중' 상태로 변경한다.")
        void fullRefundInProgress() {
            // given
            Payment payment = Payment.ofReady(request, orderInfo);
            boolean isPartialRefund = false;

            // when
            payment.refundInProgress(isPartialRefund);

            // then
            assertThat(payment.getPaymentState()).isEqualTo(PaymentState.REFUND_IN_PROGRESS);
        }
    }
}

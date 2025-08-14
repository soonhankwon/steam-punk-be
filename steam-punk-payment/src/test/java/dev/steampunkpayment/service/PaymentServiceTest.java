package dev.steampunkpayment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import dev.steampunkpayment.common.enumtype.ErrorCode;
import dev.steampunkpayment.common.exception.ApiException;
import dev.steampunkpayment.domain.OrderInfo;
import dev.steampunkpayment.domain.OrderInfo.OrderMetaData;
import dev.steampunkpayment.domain.OrderProductInfo;
import dev.steampunkpayment.domain.Payment;
import dev.steampunkpayment.domain.RegularRefundPolicy;
import dev.steampunkpayment.domain.UserPointInfo;
import dev.steampunkpayment.dto.request.PaymentAddRequest;
import dev.steampunkpayment.dto.request.PaymentExecuteRequest;
import dev.steampunkpayment.dto.response.PaymentAddResponse;
import dev.steampunkpayment.enumtype.OrderProductState;
import dev.steampunkpayment.event.publish.PaymentCompletedEvent;
import dev.steampunkpayment.repository.PaymentProductRepository;
import dev.steampunkpayment.repository.PaymentRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService 단위 테스트")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentProductRepository paymentProductRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Spy
    private RegularRefundPolicy regularRefundPolicy;

    @InjectMocks
    private PaymentService paymentService;

    @Nested
    @DisplayName("addPayment 메서드 테스트")
    class AddPaymentTest {
        @Test
        @DisplayName("결제 생성에 성공한다.")
        void addPaymentSuccess() {
            // given
            PaymentAddRequest request = new PaymentAddRequest(1L, 100L);
            OrderMetaData metaData = new OrderMetaData(100L, 1L, 5000L);
            OrderProductInfo productInfo = new OrderProductInfo(1L, 5000L, OrderProductState.REGULAR);
            OrderInfo orderInfo = new OrderInfo(metaData, List.of(productInfo));
            Payment payment = Payment.ofReady(request, orderInfo);
            ReflectionTestUtils.setField(payment, "id", 1L);

            given(paymentRepository.existsByOrderId(request.orderId())).willReturn(false);
            given(paymentRepository.save(any(Payment.class))).willReturn(payment);

            try (MockedStatic<OrderInfo> mockedOrderInfo = mockStatic(OrderInfo.class)) {
                mockedOrderInfo.when(() -> OrderInfo.fromOrderInfoInternalApi(anyLong())).thenReturn(orderInfo);

                // when
                paymentService.addPayment(request);

                // then
                ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
                verify(paymentProductRepository).saveAll(captor.capture());
                List<Payment> capturedList = captor.getValue();
                assertThat(capturedList).hasSize(1);
            }
        }

        @Test
        @DisplayName("이미 결제 내역이 존재하면 예외가 발생한다.")
        void addPaymentFailExists() {
            // given
            PaymentAddRequest request = new PaymentAddRequest(1L, 100L);
            given(paymentRepository.existsByOrderId(request.orderId())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> paymentService.addPayment(request))
                    .isInstanceOf(ApiException.class)
                    .extracting(ex -> ((ApiException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.EXISTS_PAYMENT_HISTORY_BY_ORDER_ID);
        }
    }

    @Nested
    @DisplayName("executePayment 메서드 테스트")
    class ExecutePaymentTest {
        @Test
        @DisplayName("결제 실행에 성공한다.")
        void executePaymentSuccess() {
            // given
            PaymentExecuteRequest request = new PaymentExecuteRequest(1L, 1L);
            Payment payment = mock(Payment.class);
            UserPointInfo userPointInfo = new UserPointInfo(50000L);

            given(payment.getTotalPrice()).willReturn(10000L);
            given(paymentRepository.findById(request.paymentId())).willReturn(Optional.of(payment));

            try (MockedStatic<UserPointInfo> mockedUserPointInfo = mockStatic(UserPointInfo.class)) {
                mockedUserPointInfo.when(() -> UserPointInfo.fromUserPointInfoInternalApi(anyLong()))
                        .thenReturn(userPointInfo);

                // when
                paymentService.executePayment(request);

                // then
                verify(payment).complete();
                verify(eventPublisher).publishEvent(any(PaymentCompletedEvent.class));
            }
        }

        @Test
        @DisplayName("유저 포인트가 부족하면 예외가 발생한다.")
        void executePaymentFailNotEnoughPoint() {
            // given
            PaymentExecuteRequest request = new PaymentExecuteRequest(1L, 1L);
            Payment payment = mock(Payment.class);
            UserPointInfo userPointInfo = new UserPointInfo(100L);

            given(payment.getTotalPrice()).willReturn(10000L);
            given(paymentRepository.findById(request.paymentId())).willReturn(Optional.of(payment));

            try (MockedStatic<UserPointInfo> mockedUserPointInfo = mockStatic(UserPointInfo.class)) {
                mockedUserPointInfo.when(() -> UserPointInfo.fromUserPointInfoInternalApi(anyLong()))
                        .thenReturn(userPointInfo);

                // when & then
                assertThatThrownBy(() -> paymentService.executePayment(request))
                        .isInstanceOf(ApiException.class)
                        .extracting(ex -> ((ApiException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.NOT_ENOUGH_USER_POINT);
            }
        }
    }
}

package dev.steampunkpayment.service;

import dev.steampunkpayment.common.enumtype.ErrorCode;
import dev.steampunkpayment.common.exception.ApiException;
import dev.steampunkpayment.domain.OrderInfo;
import dev.steampunkpayment.domain.Payment;
import dev.steampunkpayment.domain.UserPointInfo;
import dev.steampunkpayment.dto.request.PaymentAddRequest;
import dev.steampunkpayment.dto.response.PaymentAddResponse;
import dev.steampunkpayment.dto.response.PaymentGetResponse;
import dev.steampunkpayment.event.publish.PaymentCompletedEvent;
import dev.steampunkpayment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PaymentAddResponse addPayment(PaymentAddRequest request) {
        if (paymentRepository.existsByOrderId(request.orderId())) {
            throw new ApiException(ErrorCode.EXISTS_PAYMENT_HISTORY_BY_ORDER_ID);
        }
        OrderInfo orderInfo = getOrderInfo(request);
        // 유저의 주문인지 밸리데이션
        orderInfo.validate(request);

        UserPointInfo userPointInfo = getUserPointInfo(request);
        // 유저가 주문결제에 충분한 포인트를 가지고 있는지 밸리데이션
        userPointInfo.validatePoint(orderInfo);

        Payment payment = Payment.of(request, orderInfo);
        payment = paymentRepository.save(payment);
        eventPublisher.publishEvent(PaymentCompletedEvent.from(orderInfo));
        return PaymentAddResponse.from(payment);
    }

    private UserPointInfo getUserPointInfo(PaymentAddRequest request) {
        return WebClient.create()
                .get()
                .uri("http://localhost:8080/api/v1/users/point/" + request.userId())
                .retrieve()
                .bodyToMono(UserPointInfo.class)
                .block();
    }

    private OrderInfo getOrderInfo(PaymentAddRequest request) {
        return WebClient.create()
                .get()
                .uri("http://localhost:8080/api/v1/orders/" + request.orderId())
                .retrieve()
                .bodyToMono(OrderInfo.class)
                .block();
    }

    @Transactional(readOnly = true)
    public PaymentGetResponse findPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_PAYMENT_ID));
        return PaymentGetResponse.from(payment);
    }
}

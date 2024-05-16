package dev.steampunkpayment.service;

import dev.steampunkpayment.common.enumtype.ErrorCode;
import dev.steampunkpayment.common.exception.ApiException;
import dev.steampunkpayment.domain.OrderInfo;
import dev.steampunkpayment.domain.OrderProductInfo;
import dev.steampunkpayment.domain.Payment;
import dev.steampunkpayment.domain.PaymentRefundProduct;
import dev.steampunkpayment.domain.RefundPolicy;
import dev.steampunkpayment.domain.UserGamePlayHistoryInfo;
import dev.steampunkpayment.domain.UserPointInfo;
import dev.steampunkpayment.dto.request.PaymentAddRequest;
import dev.steampunkpayment.dto.response.PaymentAddResponse;
import dev.steampunkpayment.dto.response.PaymentGetResponse;
import dev.steampunkpayment.dto.response.RefundProgressAddResponse;
import dev.steampunkpayment.dto.response.RefundProgressGetResponse;
import dev.steampunkpayment.event.publish.PaymentCompletedEvent;
import dev.steampunkpayment.repository.PaymentRefundProductRepository;
import dev.steampunkpayment.repository.PaymentRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentRefundProductRepository paymentRefundProductRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RefundPolicy regularRefundPolicy;

    @Transactional
    public PaymentAddResponse addPayment(PaymentAddRequest request) {
        Long requestOrderId = request.orderId();
        if (paymentRepository.existsByOrderId(requestOrderId)) {
            throw new ApiException(ErrorCode.EXISTS_PAYMENT_HISTORY_BY_ORDER_ID);
        }
        OrderInfo orderInfo = getOrderInfo(requestOrderId);
        // 유저의 주문인지 밸리데이션
        orderInfo.validate(request);

        UserPointInfo userPointInfo = getUserPointInfo(request.userId());
        // 유저가 주문결제에 충분한 포인트를 가지고 있는지 밸리데이션
        userPointInfo.validatePoint(orderInfo);

        Payment payment = Payment.of(request, orderInfo);
        payment = paymentRepository.save(payment);
        eventPublisher.publishEvent(PaymentCompletedEvent.from(orderInfo));
        return PaymentAddResponse.from(payment);
    }

    private UserPointInfo getUserPointInfo(Long userId) {
        return WebClient.create()
                .get()
                .uri("http://localhost:8080/api/v1/users/point/{userId}", userId)
                .retrieve()
                .bodyToMono(UserPointInfo.class)
                .block();
    }

    private OrderInfo getOrderInfo(Long orderId) {
        return WebClient.create()
                .get()
                .uri("http://localhost:8080/api/v1/orders/{orderId}", orderId)
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

    @Transactional
    public RefundProgressAddResponse addRefundInProgress(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_PAYMENT_ID));

        // 결제 주문정보를 조회함
        OrderInfo paidOrderInfo = getOrderInfo(payment.getOrderId());
        // 결제 완료된 주문의 상품들에 대한 정보 MAP (Key: PaymentId, Value: Price - 당시 가격(스냅샷))
        Map<Long, Long> paidOrderProductInfoMap = paidOrderInfo.orderProductInfos()
                .stream()
                .collect(Collectors.toMap(OrderProductInfo::productId, OrderProductInfo::price));

        // 결제완료된 주문 상품들에 대한 플레이 기록을 가져와서 리스트로 변환
        Long userId = payment.getUserId();
        List<UserGamePlayHistoryInfo> userGamePlayHistoryInfos = paidOrderInfo.orderProductInfos()
                .stream()
                .map(orderProductInfo -> getUserGamePlayHistory(userId, orderProductInfo.productId()))
                .toList();

        // 정규 환불 정책 적용 - 환불 조건에 충족되는 상품만 결제 환불 상품에 등록(환불 IN PROCESS)
        userGamePlayHistoryInfos.stream()
                .filter(i -> i.hasRefundCondition(regularRefundPolicy))
                .forEach(i -> paymentRefundProductRepository.save(
                        PaymentRefundProduct.of(paymentId, i, paidOrderProductInfoMap.get(i.productId())))
                );

        // 상품중 하나라도 환불 조건에 충족하지 않는다면 결제는 부분 환불 대상 상태
        boolean isPartialRefund = userGamePlayHistoryInfos.stream()
                .anyMatch(i -> !i.hasRefundCondition(regularRefundPolicy));

        payment.refundInProgress(isPartialRefund);
        //TODO 환불 신청 후 D+1에 환불 완료(BATCH)
        return RefundProgressAddResponse.from(payment);
    }

    private UserGamePlayHistoryInfo getUserGamePlayHistory(Long userId, Long productId) {
        return WebClient.create()
                .get()
                .uri("http://localhost:8080/api/v1/games/{userId}?productId={productId}",
                        userId, productId)
                .retrieve()
                .bodyToMono(UserGamePlayHistoryInfo.class)
                .block();
    }

    @Transactional(readOnly = true)
    public RefundProgressGetResponse findRefundInProgress(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_PAYMENT_ID));
        List<PaymentRefundProduct> paymentRefundProducts = paymentRefundProductRepository.findAllByPaymentId(paymentId);

        long totalRefundPrice = paymentRefundProducts.stream()
                .mapToLong(PaymentRefundProduct::getPrice)
                .sum();

        return RefundProgressGetResponse.of(totalRefundPrice, payment);
    }
}

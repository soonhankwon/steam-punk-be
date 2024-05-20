package dev.steampunkpayment.service;

import dev.steampunkpayment.common.enumtype.ErrorCode;
import dev.steampunkpayment.common.exception.ApiException;
import dev.steampunkpayment.domain.OrderInfo;
import dev.steampunkpayment.domain.Payment;
import dev.steampunkpayment.domain.PaymentProduct;
import dev.steampunkpayment.domain.PaymentState;
import dev.steampunkpayment.domain.RefundPolicy;
import dev.steampunkpayment.domain.UserGamePlayHistoryInfo;
import dev.steampunkpayment.domain.UserPointInfo;
import dev.steampunkpayment.dto.request.PaymentAddRequest;
import dev.steampunkpayment.dto.request.PaymentExecuteRequest;
import dev.steampunkpayment.dto.response.PaymentAddResponse;
import dev.steampunkpayment.dto.response.PaymentGetResponse;
import dev.steampunkpayment.dto.response.RefundProgressAddResponse;
import dev.steampunkpayment.dto.response.RefundProgressGetResponse;
import dev.steampunkpayment.event.publish.PaymentCompletedEvent;
import dev.steampunkpayment.repository.PaymentProductRepository;
import dev.steampunkpayment.repository.PaymentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
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
    private final PaymentProductRepository paymentProductRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RefundPolicy regularRefundPolicy;

    // 결제 진입 비즈니스 로직: 결제대기 상태 결제 생성
    @Transactional
    public PaymentAddResponse addPayment(PaymentAddRequest request) {
        Long requestOrderId = request.orderId();
        if (paymentRepository.existsByOrderId(requestOrderId)) {
            throw new ApiException(ErrorCode.EXISTS_PAYMENT_HISTORY_BY_ORDER_ID);
        }
        OrderInfo orderInfo = getOrderInfo(requestOrderId);
        // 유저의 주문인지 밸리데이션
        orderInfo.validate(request);
        // 결제 대기 상태의 결제 생성 및 INSERT
        Payment payment = Payment.ofReady(request, orderInfo);
        payment = paymentRepository.save(payment);

        List<PaymentProduct> paymentProducts = new ArrayList<>();
        Payment finalPayment = payment;
        // 결제대기 상태의 결제 상품목록 INSERT
        orderInfo.orderProductInfos()
                .forEach(orderProductInfo -> {
                    PaymentProduct paymentProduct = PaymentProduct.of(finalPayment.getId(),
                            orderProductInfo.productId(), orderProductInfo.price(),
                            PaymentState.PAYMENT_READY);
                    paymentProducts.add(paymentProduct);
                });
        paymentProductRepository.saveAll(paymentProducts);
        return PaymentAddResponse.from(payment);
    }

    // 결제 비즈니스 로직
    @Transactional
    public PaymentAddResponse executePayment(PaymentExecuteRequest request) {
        Payment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_PAYMENT_ID));

        // 유저 마이크로 서비스로 유저 포인트 정보 요청
        UserPointInfo userPointInfo = getUserPointInfo(request.userId());
        // 유저가 주문결제에 충분한 포인트를 가지고 있는지 밸리데이션
        userPointInfo.validatePoint(payment.getTotalPrice());
        OrderInfo orderInfo = getOrderInfo(payment.getOrderId());
        Long paymentId = payment.getId();

        List<PaymentProduct> paymentProducts = paymentProductRepository.findAllByPaymentIdAndPaymentState(
                paymentId, PaymentState.PAYMENT_READY);
        // 결제대기인 상품들을 완료상태로 변경 - Dirty Check
        paymentProducts.forEach(PaymentProduct::paid);
        payment.complete();

        eventPublisher.publishEvent(PaymentCompletedEvent.from(payment, orderInfo.orderProductInfos()));
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

    // 환불 진행 API
    @Transactional
    public RefundProgressAddResponse addRefundInProgress(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_PAYMENT_ID));

        // 결제완료 상태인 결제 상품목록을 조회
        List<PaymentProduct> paymentProducts = paymentProductRepository.findAllByPaymentIdAndPaymentState(
                payment.getId(),
                PaymentState.PAYMENT_COMPLETED
        );

        // 결제완료된 주문 상품들에 대한 플레이 기록을 가져와서 리스트로 변환
        Long userId = payment.getUserId();
        List<UserGamePlayHistoryInfo> userGamePlayHistoryInfos = paymentProducts
                .stream()
                .map(paymentProduct -> getUserGamePlayHistory(userId, paymentProduct.getProductId()))
                .toList();

        // 정규 환불 정책 적용 - 환불 조건에 충족되는 상품만 결제 환불 진행 상태로 업데이트(환불 IN PROCESS)
        AtomicReference<Long> refundTotalPrice = new AtomicReference<>(0L);
        userGamePlayHistoryInfos.stream()
                .filter(i -> i.hasRefundCondition(regularRefundPolicy))
                .forEach(i -> {
                            PaymentProduct paymentProduct = paymentProductRepository.findByPaymentIdAndProductId(paymentId,
                                            i.productId())
                                    .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_PAYMENT_ID));
                            paymentProduct.refundInProgress();
                            Long refundPrice = paymentProduct.getPrice();
                            refundTotalPrice.updateAndGet(v -> v + refundPrice);
                        }
                );

        // 상품중 하나라도 환불 조건에 충족하지 않는다면 결제는 부분 환불 대상 상태
        boolean isPartialRefund = userGamePlayHistoryInfos.stream()
                .anyMatch(i -> !i.hasRefundCondition(regularRefundPolicy));

        payment.refundInProgress(isPartialRefund);
        //TODO 환불 신청 후 D+1에 환불 완료(BATCH)
        return RefundProgressAddResponse.of(refundTotalPrice.get(), payment);
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
        List<PaymentProduct> paymentRefundProducts = paymentProductRepository.findAllByPaymentIdAndPaymentState(
                paymentId,
                PaymentState.PAYMENT_REFUND_IN_PROGRESS
        );

        long totalRefundPrice = paymentRefundProducts.stream()
                .mapToLong(PaymentProduct::getPrice)
                .sum();

        return RefundProgressGetResponse.of(totalRefundPrice, payment);
    }

    //TODO 유저의 결제정보가 많은 경우 대비 페이지네이션 필요
    @Transactional(readOnly = true)
    public List<PaymentGetResponse> findUserPayments(Long userId) {
        return paymentRepository.findAllByUserId(userId)
                .stream()
                .map(PaymentGetResponse::from)
                .collect(Collectors.toList());
    }
}

package dev.steampunkpayment.event.publish;

import dev.steampunkpayment.domain.OrderProductInfo;
import dev.steampunkpayment.dto.request.OrderStateUpdateRequest;
import dev.steampunkpayment.dto.request.UserGameHistoryAddRequest;
import dev.steampunkpayment.dto.request.UserPointDecreaseRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Component
public class PaymentCompletedEventPublisher {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishPaymentCompletedEvent(PaymentCompletedEvent paymentCompletedEvent) {
        Long orderId = paymentCompletedEvent.payment().getOrderId();
        Long userId = paymentCompletedEvent.payment().getUserId();
        Long paidTotalPrice = paymentCompletedEvent.payment().getTotalPrice();
        assert orderId != null && userId != null;

        List<Long> productIds = paymentCompletedEvent.orderProductInfos()
                .stream()
                .map(OrderProductInfo::productId)
                .collect(Collectors.toList());

        updateOrderState(orderId,
                OrderStateUpdateRequest.of(userId, "ORDER_PAYMENT_COMPLETED")
        );

        decreaseUserPoint(userId,
                UserPointDecreaseRequest.from(paidTotalPrice)
        );

        addUserGameHistory(
                UserGameHistoryAddRequest.of(userId, productIds)
        );
    }

    private void updateOrderState(Long orderId, OrderStateUpdateRequest request) {
        WebClient.create()
                .patch()
                .uri("http://localhost:8080/api/v1/orders/{orderId}", orderId)
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private void decreaseUserPoint(Long userId, UserPointDecreaseRequest request) {
        WebClient.create()
                .patch()
                .uri("http://localhost:8080/api/v1/users/point/{userId}/decrease", userId)
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private void addUserGameHistory(UserGameHistoryAddRequest request) {
        WebClient.create()
                .post()
                .uri("http://localhost:8080/api/v1/games")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}

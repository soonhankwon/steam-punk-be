package dev.steampunkpayment.event.publish;

import dev.steampunkpayment.domain.OrderInfo;
import dev.steampunkpayment.dto.request.OrderStateUpdateRequest;
import dev.steampunkpayment.dto.request.UserGameHistoryAddRequest;
import dev.steampunkpayment.dto.request.UserPointDecreaseRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PaymentCompletedEventPublisher {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishPaymentCompletedEvent(PaymentCompletedEvent paymentCompletedEvent) {
        OrderInfo orderInfo = paymentCompletedEvent.orderInfo();
        Long orderId = orderInfo.orderId();
        Long userId = orderInfo.userId();
        assert orderId != null && userId != null;

        updateOrderState(orderId,
                OrderStateUpdateRequest.of(userId, "ORDER_PAYMENT_COMPLETED")
        );

        decreaseUserPoint(userId,
                UserPointDecreaseRequest.from(orderInfo)
        );

        addUserGameHistory(
                UserGameHistoryAddRequest.of(userId, orderInfo.orderProductIds())
        );
    }

    private void updateOrderState(Long orderId, OrderStateUpdateRequest request) {
        WebClient.create()
                .patch()
                .uri("http://localhost:8080/api/v1/orders/" + orderId)
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private void decreaseUserPoint(Long userId, UserPointDecreaseRequest request) {
        WebClient.create()
                .patch()
                .uri("http://localhost:8080/api/v1/users/point/" + userId + "/decrease")
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

package dev.steampunkpayment.event.publish;

import dev.steampunkpayment.dto.request.OrderStateUpdateRequest;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Component
public class PaymentFailedEventPublisher {

    private static final String ORDER_CANCELED_STATE = "ORDER_CANCEL";

    @EventListener
    public void publishPaymentFailedEvent(PaymentFailedEvent paymentFailedEvent) {
        Long orderId = Objects.requireNonNull(paymentFailedEvent.orderInfo().orderMetaData().orderId());
        Long userId = Objects.requireNonNull(paymentFailedEvent.orderInfo().orderMetaData().userId());

        Set<Long> limitedProductIds = Objects.requireNonNull(paymentFailedEvent.tempLimitedProductIdsSet());
        updateOrderState(orderId,
                OrderStateUpdateRequest.of(userId, ORDER_CANCELED_STATE)
        );
        limitedProductIds.forEach(this::increaseProductStock);
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

    private void increaseProductStock(Long productId) {
        WebClient.create()
                .patch()
                .uri("http://localhost:8080/api/v1/stock/{productId}/increase", productId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}

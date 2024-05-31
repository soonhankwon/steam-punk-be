package dev.steampunkpayment.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkpayment.common.enumtype.ErrorCode;
import dev.steampunkpayment.common.exception.ApiException;
import dev.steampunkpayment.dto.request.PaymentAddRequest;
import java.util.List;
import org.springframework.web.reactive.function.client.WebClient;

public record OrderInfo(
        @JsonProperty("meta")
        OrderMetaData orderMetaData,
        @JsonProperty("documents")
        List<OrderProductInfo> orderProductInfos
) {
    public record OrderMetaData(
            @JsonProperty("order_id")
            Long orderId,
            @JsonProperty("user_id")
            Long userId,
            @JsonProperty("total_price")
            Long totalPrice
    ) {
    }

    public void validate(PaymentAddRequest request) {
        if (!isRequestUserIdEqualsOrderInfoUserId(request)) {
            throw new ApiException(ErrorCode.INVALID_REQUEST_ORDER_USER_ID);
        }
    }

    private boolean isRequestUserIdEqualsOrderInfoUserId(PaymentAddRequest request) {
        return request.userId().equals(this.orderMetaData.userId());
    }

    public static OrderInfo fromOrderInfoInternalApi(Long orderId) {
        return WebClient.create()
                .get()
                .uri("http://localhost:8080/api/v1/orders/{orderId}", orderId)
                .retrieve()
                .bodyToMono(OrderInfo.class)
                .block();
    }
}

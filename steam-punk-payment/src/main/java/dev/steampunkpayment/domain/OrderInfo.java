package dev.steampunkpayment.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkpayment.common.enumtype.ErrorCode;
import dev.steampunkpayment.common.exception.ApiException;
import dev.steampunkpayment.dto.request.PaymentAddRequest;
import java.util.List;

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
}

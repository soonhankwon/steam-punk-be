package dev.steampunkpayment.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.steampunkpayment.common.enumtype.ErrorCode;
import dev.steampunkpayment.common.exception.ApiException;
import dev.steampunkpayment.dto.request.PaymentAddRequest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("OrderInfo 도메인 단위 테스트")
class OrderInfoTest {

    @Test
    @DisplayName("요청 유저 ID와 주문 정보의 유저 ID가 같으면 검증에 성공한다.")
    void validateSuccess() {
        // given
        Long userId = 1L;
        Long orderId = 100L;
        OrderInfo.OrderMetaData metaData = new OrderInfo.OrderMetaData(orderId, userId, 5000L);
        OrderInfo orderInfo = new OrderInfo(metaData, List.of());
        PaymentAddRequest request = new PaymentAddRequest(userId, orderId);

        // when & then
        assertThatCode(() -> orderInfo.validate(request))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("요청 유저 ID와 주문 정보의 유저 ID가 다르면 예외가 발생한다.")
    void validateFailure() {
        // given
        Long orderUserId = 1L;
        Long requestUserId = 2L;
        Long orderId = 100L;
        OrderInfo.OrderMetaData metaData = new OrderInfo.OrderMetaData(orderId, orderUserId, 5000L);
        OrderInfo orderInfo = new OrderInfo(metaData, List.of());
        PaymentAddRequest request = new PaymentAddRequest(requestUserId, orderId);

        // when & then
        assertThatThrownBy(() -> orderInfo.validate(request))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_REQUEST_ORDER_USER_ID);
    }
}

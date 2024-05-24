package dev.steampunkpayment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.steampunkpayment.dto.PaymentDTO;
import java.util.List;

public record PaymentsGetResponse(
        @JsonProperty("meta")
        PaymentMetaData paymentMetaData,
        @JsonProperty("documents")
        List<PaymentDTO> paymentDTOS
) {
    public record PaymentMetaData(
            @JsonProperty("user_id")
            Long userId,
            @JsonProperty("total_count")
            Long totalCount,
            @JsonProperty("pageable_count")
            Integer pageableCount,
            @JsonProperty("is_end")
            Boolean isEnd
    ) {
    }

    public static PaymentsGetResponse of(PaymentMetaData metaData, List<PaymentDTO> paymentDTOS) {
        return new PaymentsGetResponse(
                metaData,
                paymentDTOS
        );
    }
}

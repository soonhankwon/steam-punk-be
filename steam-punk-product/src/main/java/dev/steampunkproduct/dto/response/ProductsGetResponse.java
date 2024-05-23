package dev.steampunkproduct.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ProductsGetResponse(
        ProductsMetaData meta,
        List<ProductGetResponse> documents
) {
    public static ProductsGetResponse of(ProductsMetaData metaData, List<ProductGetResponse> productGetResponses) {
        return new ProductsGetResponse(
                metaData,
                productGetResponses
        );
    }

    public record ProductsMetaData(
            @JsonProperty("total_count")
            Long totalCount,
            @JsonProperty("pageable_count")
            Integer pageableCount,
            @JsonProperty("is_end")
            Boolean isEnd
    ) {
    }
}

package dev.steampunkproductbatch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductRawInfo(
        String name,
        @JsonProperty("release_date")
        String releaseDate,
        Double price
) {
}

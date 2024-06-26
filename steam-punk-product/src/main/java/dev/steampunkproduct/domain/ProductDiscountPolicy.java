package dev.steampunkproduct.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductDiscountPolicy {
    
    REGULAR(1.0),
    SEVENTY_FIVE_PERCENT_SALE(0.75),
    HALF_SALE(0.5),
    TWENTY_FIVE_PERCENT_SALE(0.25);

    private final double saleRate;
}

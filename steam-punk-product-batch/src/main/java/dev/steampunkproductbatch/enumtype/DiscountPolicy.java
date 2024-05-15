package dev.steampunkproductbatch.enumtype;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiscountPolicy {

    REGULAR,
    SEVENTY_FIVE_PERCENT_SALE,
    HALF_SALE,
    TWENTY_FIVE_PERCENT_SALE;
}

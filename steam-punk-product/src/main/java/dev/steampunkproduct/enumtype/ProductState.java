package dev.steampunkproduct.enumtype;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductState {

    ON_SALE_EVENT,
    ON_SALE_LIMITED_STOCK_EVENT,
    LIMITED_STOCK_EVENT,
    REGULAR
}

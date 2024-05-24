package dev.steampunkproduct.enumtype;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductState {

    ON_SALE,
    ON_SALE_LIMITED_STOCK,
    LIMITED_STOCK,
    REGULAR
}

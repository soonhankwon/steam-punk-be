package dev.steampunkpayment.dto.request;

public record UserPointDecreaseRequest(
        Long point
) {
    public static UserPointDecreaseRequest from(Long paidTotalPrice) {
        return new UserPointDecreaseRequest(paidTotalPrice);
    }
}

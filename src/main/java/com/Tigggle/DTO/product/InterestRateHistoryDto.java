package com.Tigggle.DTO.product;

import com.Tigggle.Constant.ProductType;
import com.Tigggle.Entity.product.InterestRateHistory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter

public class InterestRateHistoryDto {

    private float interestRate; // 변동 금리
    private LocalDate date; // 금리 적용 날짜
    private ProductType productType; // 상품 타입

    public static InterestRateHistoryDto from(InterestRateHistory interestRateHistory) {

        InterestRateHistoryDto interestRateHistoryDto = new InterestRateHistoryDto();

        interestRateHistoryDto.setInterestRate(interestRateHistory.getInterestRate());
        interestRateHistoryDto.setDate(interestRateHistory.getDate());
        interestRateHistoryDto.setProductType(interestRateHistory.getProduct().getProductType());

        return interestRateHistoryDto;
    }
}

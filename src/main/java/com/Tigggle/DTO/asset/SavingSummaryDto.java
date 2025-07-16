package com.Tigggle.DTO.asset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SavingSummaryDto {

    private Long depositTotal;     // 예금
    private Long savingTotal;      // 적금
    private Long accountTotal;     // 입출금 계좌
    private Long total;            // 전체 총합
}

package com.Tigggle.DTO.insite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KeywordMonthlySpendingDto {

    private String keyword;           // majorKeyword
    private List<Long> monthlyAmount; // 최근 6개월치 소비금액
}

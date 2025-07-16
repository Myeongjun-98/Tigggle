package com.Tigggle.DTO.asset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthTotalDto {
    private Long totalSpent;    // 누적 지출
    private Long monthlySpent;  // 이번 달 지출
    private Long dailySpent;    // 오늘 지출
}

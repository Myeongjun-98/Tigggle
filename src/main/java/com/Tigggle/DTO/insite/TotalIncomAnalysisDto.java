package com.Tigggle.DTO.insite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TotalIncomAnalysisDto {
    private Long MonthAmount; //이달 소비금액
    private Long TodayAmount; //오늘 소비금액
    private Long CumulativeAmount; // 누적소비금액(올해)
}

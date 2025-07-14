package com.Tigggle.DTO.asset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionHabitDto {

    private String mostSpentCategory;       // 가장 많이 쓴 카테고리
    private String leastSpentCategory;      // 가장 적게 쓴 카테고리
    private String mostAlignedCategory;     // 목표와 가장 유사한 카테고리
}

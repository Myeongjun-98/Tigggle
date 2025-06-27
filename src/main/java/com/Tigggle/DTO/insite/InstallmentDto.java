package com.Tigggle.DTO.insite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InstallmentDto {
    private Long MonthIncome; //이번달 수익
    private Long MonthExpense; //이번달 내 지출
}

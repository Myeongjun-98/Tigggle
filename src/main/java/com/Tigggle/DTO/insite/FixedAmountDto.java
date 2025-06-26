package com.Tigggle.DTO.insite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class FixedAmountDto {
    private Long fixedIncome; //고정수익
    private Long fiedExpense; //고정지출
}

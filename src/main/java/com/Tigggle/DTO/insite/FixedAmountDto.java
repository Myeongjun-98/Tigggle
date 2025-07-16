package com.Tigggle.DTO.insite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class FixedAmountDto {
    private Long income;     // 고정수익 (급여)
    private Long saving;     // 고정저축 (저축/보험)
    private Double saveRate;  // 저축률 (%)
}

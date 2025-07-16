package com.Tigggle.DTO.insite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MonthlyDateDto {
    // 막대 하나 표현 dto
    Integer whatMonth; //몇월인지
    Long totalAmount; //해당 월 총 지출액
}

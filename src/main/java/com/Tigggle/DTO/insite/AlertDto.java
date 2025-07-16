package com.Tigggle.DTO.insite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class AlertDto {
    private String keyword; //키워드
    private Long expenseAmount; //사용금액
    private Long excessAmount; //초과금액

}

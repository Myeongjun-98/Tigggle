package com.Tigggle.DTO.insite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SoultionByKeywordDto {
    private String keyword; //키워드
    private Long goalAmount; //목표금액
    private Long currentAmount; //현재금액
    private Long excessAmount; //초과금액
}

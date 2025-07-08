package com.Tigggle.DTO.insite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AgeGroupAverageDto {

    private String ageGroup;     // ex) "20~30대"
    private Long averageAmount;  // 해당 그룹의 평균 소비금액
}

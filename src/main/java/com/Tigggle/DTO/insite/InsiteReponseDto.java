package com.Tigggle.DTO.insite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InsiteReponseDto {
    // 페이지 전체 리스트 dto
    Long totalAmountInPeriod; // 6개월 전체 지출 합계
    List<MonthlyDateDto> monthlyDateDtoList; //월별 지출 데이터 리스트
}

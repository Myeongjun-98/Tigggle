package com.Tigggle.DTO.asset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavingSuggestionDto {

    private double americanoCount;   // 아메리카노 덜 마시기
    private double alcoholCount;     // 술자리 덜 가기
    private double travelCount;      // 해외여행 덜 가기
    private double luxuryCount;      // 명품 덜 사기
}

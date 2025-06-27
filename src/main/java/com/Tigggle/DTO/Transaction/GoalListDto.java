package com.Tigggle.DTO.Transaction;

import com.Tigggle.Entity.Transaction.Keywords;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GoalListDto {
    private Keywords keyword;       // 분류
    private String description;     // 상세
    private Long amount;            // 금액
    private String note;            // 메모
}

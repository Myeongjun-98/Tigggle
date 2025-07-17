package com.Tigggle.DTO.Transaction;

import com.Tigggle.Entity.Transaction.Goal;
import com.Tigggle.Entity.Transaction.Keywords;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor @AllArgsConstructor
public class GoalDto {
    private Long id;
    private String description;     // 상세
    private String keyword;       // 분류
    private Long amount;            // 금액
    private String note;            // 메모

    private Long keywordId;

}

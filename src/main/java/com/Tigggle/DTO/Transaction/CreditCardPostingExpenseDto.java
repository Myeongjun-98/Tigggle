package com.Tigggle.DTO.Transaction;

import java.time.LocalDate;

import com.Tigggle.Entity.Transaction.Keywords;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreditCardPostingExpenseDto {
    private String description;     //  내용
    private LocalDate paidDay;      // 결제일
    private Keywords keyword;       // 분류
    private Long amount;            // 금액
    private int installment;        // 할부 개월
    private String note;            // 메모
}

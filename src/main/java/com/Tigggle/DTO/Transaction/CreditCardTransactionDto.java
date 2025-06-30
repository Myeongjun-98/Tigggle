package com.Tigggle.DTO.Transaction;

import java.time.LocalDate;

import com.Tigggle.Entity.Transaction.Keywords;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class CreditCardTransactionDto {
    private String description;             // 내용
    private LocalDate transactionDate;      // 결제 예정일(신용카드 정산일)
    private Keywords keyword;               // 분류
    private Long originalAmount;            // 초기 결제금액
    private Long nextPayAmount;             // 결제 예정금액
    private Long leftToPayAmount;           // 남은 결제금액
    private String note;                    // 메모
}

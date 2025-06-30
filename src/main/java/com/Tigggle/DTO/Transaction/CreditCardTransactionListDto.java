package com.Tigggle.DTO.Transaction;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreditCardTransactionListDto {
    private String description;         // 내용
    private LocalDate transactionDate;  // 결제 예정일(신용카드 정산일)
    private Long nextPayAmount;         // 결제 예정금액
}

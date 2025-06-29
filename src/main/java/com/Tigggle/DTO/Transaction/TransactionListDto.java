package com.Tigggle.DTO.Transaction;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TransactionListDto {
    private boolean isConsumption;          // 지출 여부
    private String description;             // 내용
    private Long amount;                    // 금액
    private LocalDate transactionDate;      // 내역 발생 일시
}

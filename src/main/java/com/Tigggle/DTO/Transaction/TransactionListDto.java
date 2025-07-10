package com.Tigggle.DTO.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TransactionListDto {
    private Long id;                        // 디테일 조회용
    private boolean isConsumption;          // 지출 여부
    private String description;             // 내용
    private Long amount;                    // 금액
    private LocalDateTime transactionDate;      // 내역 발생 일시
}

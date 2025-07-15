package com.Tigggle.DTO.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.Tigggle.Constant.Transaction.PayMethod;
import com.Tigggle.Entity.Transaction.Keywords;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor @AllArgsConstructor
public class TransactionUpdateDto {
    @NotNull
    private boolean isConsumption;      // "INCOME" or "EXPENSE"
    @NotBlank
    private String description;         // 내용
    @NotNull
    private Long amount;                // 금액
    @NotNull
    private LocalDateTime transactionDate;  // 내역 발생 일시

    private Long keywordId;           // 분류
    private String note;                // 메모

}

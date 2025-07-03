package com.Tigggle.DTO.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.Tigggle.Constant.Transaction.PayMethod;
import com.Tigggle.Entity.Transaction.Keywords;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor @AllArgsConstructor
public class TransactionDetailDto {
    private boolean isConsumption;      // 지출 여부
    private String description;         // 내용
    private Long amount;                // 금액
    private LocalDateTime transactionDate;  // 내역 발생 일시
    private Keywords keyword;           // 분류
    private PayMethod payMethod;        // 결제 수단
    private String note;                // 메모
    private int installment;            // 할부 개월
}


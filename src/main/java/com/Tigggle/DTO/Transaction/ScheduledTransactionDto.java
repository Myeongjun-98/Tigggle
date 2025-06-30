package com.Tigggle.DTO.Transaction;

import java.time.LocalDate;

import com.Tigggle.Constant.Transaction.Frequency;
import com.Tigggle.Constant.Transaction.PayMethod;
import com.Tigggle.Entity.Transaction.Keywords;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ScheduledTransactionDto {
    private boolean isConsumption;          // 지출 여부
    private String description;             // 내용
    private Long amount;                    // 금액
    private Keywords keyword;               // 분류
    private PayMethod payMethod;            // 결제 수단
    private String note;                    // 메모
    private boolean reflectOnAsset;         // 자산 반영 여부
    private Frequency frequency;            // 주기
    private int dayOfExcution;              // 실행 기준일
    private LocalDate starDate;             // 시작일
    private LocalDate endDate;              // 종료일
    private LocalDate nextExcutionDate;     // 다음 실행 예정일
    private boolean isActive;               // 활성 여부
}
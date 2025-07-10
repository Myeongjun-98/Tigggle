package com.Tigggle.DTO.Transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionCreateRequestDto {

    // --- 공통 & 수입 정보 ---
    @NotNull
    private String transactionType; // "INCOME" or "EXPENSE"
    @NotBlank
    private String description; // 내용
    @NotNull
    private Long amount; // 금액
    @NotNull
    private LocalDateTime transactionDate; // 날짜 및 시간

    private Long keywordId; // 키워드 ID
    private String note; // 메모


    // --- 지출(EXPENSE) 시 추가 정보 ---
    private String payMethod; // "CASH", "NORMAL", "MYACCOUNT", "CHECK_CARD", "CREDIT_CARD"
    private Long sourceAssetId; // 돈이 나간 자산(계좌/현금) ID, 주체가 되는 자산

    private Long destinationAssetId;

    // --- 신용카드(CREDIT_CARD) 지출 시 추가 정보 ---
    private Long creditCardId; // 사용한 신용카드 ID
    private int installment; // 할부 개월 수 (일시불이면 0 또는 1)

}
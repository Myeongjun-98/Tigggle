package com.Tigggle.DTO.Transaction;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor @AllArgsConstructor
public class InstallmentSavingDto {
    private String alias;                   // 적금 별칭
    private Long balance;                   // 적금 잔액
    private LocalDate openDate;             // 계좌 개설일
    private String accountNumber;           // 계좌 번호
    private String bankName;                // 은행 이름
    private String bankLogo;                // 은행 로고
    private float interest;                 // 적금 이자율
    private boolean isCompound;             // 복리 여부
    private LocalDate expireDay;            // 만기일
    private Long monthlyPaymentAmount;      // 월 납입금
    private int paymentDay;                 // 납입일
    private int currentPaymentCount;        // 현재 납입 횟수
}

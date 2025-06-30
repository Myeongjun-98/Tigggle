package com.Tigggle.DTO.Transaction;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor @AllArgsConstructor
public class DepositDto {
    private String alias;           // 정기예금 별칭
    private Long balance;           // 정기예금 잔액
    private LocalDate openDate;     // 계좌 개설일
    private String accountNumber;   // 계좌번호
    private String bankName;        // 은행 이름
    private String bankLogo;        // 은행 로고
    private float interest;         // 정기예금 이자율
    private boolean isCompound;     // 복리 여부
    private LocalDate expireDay;    // 만기일
}

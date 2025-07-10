package com.Tigggle.DTO.Transaction;

import com.Tigggle.Constant.Transaction.LimitType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor @AllArgsConstructor
public class OrdinaryAccountDto {
    private String alias;           // 보통예금 별칭
    private Long balance;           // 보통예금 잔액
    private String accountNumber;    // 계좌번호
    private String bankName;        // 은행 이름
    private String bankLogo;        // 은행 로고
    private float interest;         // 이자율
    private boolean isCompound;     // 복리여부
    private Long expenseLimit;      // 출금 한도
    private LimitType limitType;    // 출금한도 종류
}

package com.Tigggle.DTO.Transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreditCardDto {
    private String cardName;        // 카드 이름
    private String cardNumber;      // 카드 번호
    private String linkedAccount;   // 연결된 (입출금) 계좌
    private int expenseDay;         // 출금일
    private float interest;         // 이자율
    private Long cardLimit;         // 출금 한도
}

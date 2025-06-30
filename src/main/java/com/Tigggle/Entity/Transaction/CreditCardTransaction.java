package com.Tigggle.Entity.Transaction;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

@Entity @Setter @Getter
public class CreditCardTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // 아이디

    private Card card;                  // 신용카드 정보

    private String description;         // 내용

    private LocalDate transactionDate;  // 내역 발생시기

    private Keywords keyword;           // 분류

    private Long originalAmount;        // 원 결제금액
    
    private Long nextPayAmount;         // 다음 결제 예정금액

    private Long leftToPayAmount;       // 남은 결제금액

    @Lob
    private String note;                // 메모
}

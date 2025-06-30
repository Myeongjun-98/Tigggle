package com.Tigggle.Entity.Transaction;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Setter @Getter
public class CreditCardTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Card card;                  // 신용카드 정보

    private String description;         // 내용

    @Column(nullable = false)
    private LocalDate transactionDate;  // 내역 발생시기

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private Keywords keyword;           // 분류

    @Column(nullable = false)
    private Long originalAmount;        // 원 결제금액

    @Column(nullable = false)
    private Long nextPayAmount;         // 다음 결제 예정금액

    @Column(nullable = false)
    private Long leftToPayAmount;       // 남은 결제금액

    @Lob
    private String note;                // 메모
}

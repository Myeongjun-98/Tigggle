package com.Tigggle.Entity.Transaction;

import java.time.LocalDate;

import com.Tigggle.Constant.Transaction.PayMethod;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long id;                            // 거래내역 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Asset asset;                        // 자산 정보 ( 거래 수단, 카드를 포함 )

    private String description;                 // 내용

    @Column(nullable=false)
    private LocalDate transactionDate;          // 내역 발생 일시

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Keywords keyword;                   // 분류

    @Column(nullable = false)
    private boolean isConsumption;              // 지출/수익

    @Column(nullable = false)
    private Long amount = 0L;                   // 금액

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PayMethod payMethod;                // 결제 수단

    @Lob
    private String note;                        // 메모

    @Column(nullable = false)
    private boolean reflectOnAsset = true;      // 자산 반영 여부

    private int installment;                    // 할부 개월 수
}

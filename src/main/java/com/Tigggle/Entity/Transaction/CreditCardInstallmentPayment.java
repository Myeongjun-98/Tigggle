package com.Tigggle.Entity.Transaction;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity @Getter @Setter
//* 신용카드 할부에 대한 예정 거래내역을 미리 계산해서 담아두는 엔티티.
public class CreditCardInstallmentPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 할부 청구 내역의 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_transaction_id", nullable = false)
    private CreditCardTransaction originalTransaction; // 원본 거래내역 참조

    @Column(nullable = false)
    private Long paymentAmount; // 이번 회차에 청구될 금액 (나누어진 금액)

    @Column(nullable = false)
    private LocalDate paymentDueDate; // 이 금액이 청구될 날짜 (결제일)

    @Column(nullable = false)
    private int sequence; // 할부 회차 (예: 3개월 중 1번째)

    @Column(nullable = false)
    private boolean isSettled = false; // 이 회차의 대금이 정산되었는지 여부
}
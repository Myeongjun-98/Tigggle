package com.Tigggle.Entity.Transaction;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Card extends Asset{
    private String cardName;            // 카드 이름(별칭)

    private String cardNumber;          // 카드 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private BankAccount bankAccount;    // 연결된 계좌
}

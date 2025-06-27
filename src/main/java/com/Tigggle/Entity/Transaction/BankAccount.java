package com.Tigggle.Entity.Transaction;

import com.Tigggle.Entity.product.Bank;
import org.hibernate.annotations.ColumnDefault;

import com.Tigggle.Constant.Transaction.LimitType;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class BankAccount extends Asset{
    private String accountNumber;       // 계좌번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Bank bank;                  // 은행

    @ColumnDefault("0")
    private float interest = 0;             // 이자율

    @Column(nullable = false)
    private boolean isCompound = false;         // 복리 여부

    private Long expenseLimit;          // 출금 한도

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LimitType limitType = LimitType.MONTHLY;        // 출금한도 종류

    private Long balance = 0L;               // 잔액
}

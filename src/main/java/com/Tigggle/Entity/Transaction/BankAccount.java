package com.Tigggle.Entity.Transaction;

import com.Tigggle.Entity.product.Bank;

import com.Tigggle.Constant.Transaction.LimitType;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class BankAccount extends Asset{
    private String accountNumber;       // 계좌번호

    private Bank bank;                  // 은행

    private float interest = 0;             // 이자율

    private boolean isCompound = false;         // 복리 여부

    private Long expenseLimit;          // 출금 한도

    private LimitType limitType = LimitType.MONTHLY;        // 출금한도 종류

    private Long balance = 0L;               // 잔액
}

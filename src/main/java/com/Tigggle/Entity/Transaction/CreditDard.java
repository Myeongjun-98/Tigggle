package com.Tigggle.Entity.Transaction;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter @DiscriminatorValue("CREDIT")
public class CreditDard extends Card{

    @Column(nullable = false)
    private int expenseDay;             // 만기일

    @Column(nullable = false)
    private float creditCardInterest;   // 신용카드 이자율

    private Long creditCardLimit = null;// 신용카드 금액한도
}

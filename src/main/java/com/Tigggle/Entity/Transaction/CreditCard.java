package com.Tigggle.Entity.Transaction;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter @DiscriminatorValue("CREDIT")
public class CreditCard extends Card{

    private int expenseDay;             // 정산일

    private float creditCardInterest;   // 신용카드 이자율

    private Long creditCardLimit;// 신용카드 금액한도
}

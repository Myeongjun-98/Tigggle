package com.Tigggle.Entity.Transaction;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "card_type")
public class Card extends Asset{
    private String cardName;            // 카드 이름(별칭)

    private String cardNumber;          // 카드 번호

    private BankAccount bankAccount;    // 연결된 계좌
}

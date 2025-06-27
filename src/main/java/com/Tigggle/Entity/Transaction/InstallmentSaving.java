package com.Tigggle.Entity.Transaction;

import java.time.LocalDate;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity
@DiscriminatorValue("SAVINGS")
public class InstallmentSaving extends BankAccount{

    @Column(nullable = false)
    private LocalDate expireDay;            // 적금 만기일

    @ColumnDefault("0")
    private Long monthlyPaymentAmount = 0L; // 1달 납부액

    @Column(nullable = false)
    private int paymentDay;                 // 납부일

    @ColumnDefault("0")
    private int currentPaymentCount = 0;    // 누적 납부횟수

}

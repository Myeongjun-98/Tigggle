package com.Tigggle.Entity.Transaction;

import java.time.LocalDate;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity
@DiscriminatorValue("SAVINGS")
public class InstallmentSaving extends BankAccount{

    private LocalDate expireDay;            // 적금 만기일

    private Long monthlyPaymentAmount; // 1달 납부액

    private int paymentDay;                 // 납부일

    private int currentPaymentCount;    // 누적 납부횟수

}

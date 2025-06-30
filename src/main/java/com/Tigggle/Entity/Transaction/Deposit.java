package com.Tigggle.Entity.Transaction;

import java.time.LocalDate;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity @Setter @Getter
@DiscriminatorValue("DEPOSIT")
public class Deposit extends BankAccount{

    private LocalDate expireDay;    // 예금 만기일

}

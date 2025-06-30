package com.Tigggle.Entity.Transaction;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
@DiscriminatorValue("CASH")
public class Cash extends Asset{
    private Long balance = 0L;   // 잔액
}

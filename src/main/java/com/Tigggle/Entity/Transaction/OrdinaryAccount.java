package com.Tigggle.Entity.Transaction;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
@DiscriminatorValue("ORDINARY")
public class OrdinaryAccount extends BankAccount{

}

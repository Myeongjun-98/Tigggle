package com.Tigggle.Entity.Transaction;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue("CHECK")
public class CheckCard extends Card{
    private Long dailyLimit; // 1일 이용 한도금액
}

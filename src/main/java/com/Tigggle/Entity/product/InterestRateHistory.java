package com.Tigggle.Entity.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter

public class InterestRateHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 금리변동 기록 일련번호

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // 금융상품 일련번호

    private float interestRate; // 금리 변동 기록

    private LocalDate date; // 변동 기록일
}

package com.Tigggle.Entity.gold;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter

public class Gold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 금 목록 일련번호

    private float weight; // 금 무게(기준)

    private String weightUnit; // 무게 단위

    private float price; // 가격

    private String priceUnit; // 가격 단위

    private LocalDateTime changeDate; // 가격변동일
}

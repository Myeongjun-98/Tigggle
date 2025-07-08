package com.Tigggle.Entity.Transaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity @Setter @Getter
public class Keywords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // 아이디

    @Column(nullable = false)
    private String majorKeyword;    // 대 카테고리
    private String minorKeyword;    // 소 카테고리
}

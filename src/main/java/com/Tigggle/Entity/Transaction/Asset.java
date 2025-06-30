package com.Tigggle.Entity.Transaction;

import java.time.LocalDate;

import com.Tigggle.Entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
@Inheritance(strategy =InheritanceType.SINGLE_TABLE )   // 단일 테이블 전략
@DiscriminatorColumn(name = "asset_type")
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // 자산 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;          // 유저 정보

    @Column(nullable = false)
    private String alias;       // 자산 별칭
    
    @Column(nullable = false)
    private LocalDate openDate; // 자산 등록일
}

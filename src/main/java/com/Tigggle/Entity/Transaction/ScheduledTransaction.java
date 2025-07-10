package com.Tigggle.Entity.Transaction;

import java.time.LocalDate;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import com.Tigggle.Constant.Transaction.Frequency;
import com.Tigggle.Constant.Transaction.PayMethod;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter @Entity
public class ScheduledTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                        // 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Asset asset;                    // 자산 정보

    private String description;             // 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Keywords keyword;               // 분류(키워드)

    @Column(nullable = false)
    private boolean isConsumption;          // 지출/소비 

    @ColumnDefault("0")
    private Long amount;                    // 금액

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PayMethod payMethod;            // 지불 방식

    @Lob
    private String note;                    // 메모

    @Column(nullable = false)
    private boolean reflectOnAsset;         // 자산 반영 여부

    @Column(nullable = false)
    private Frequency frequency;            // 정기 입/출금 주기

    @Column(nullable = false)
    private LocalDate dayOfExcution;        // 실행 기준일(월 반복시 '일', 주 반복시 '요일')

    @Column(nullable = false)           
    private LocalDate startDate;            // 시작일

    private LocalDate endDate;              // 종료일
    private LocalDate nextExcutionDate;     // 다음 실행 예정일(스케쥴러가 받는 날짜)
    
    @Column(nullable = false)   
    private boolean isAcive;                // 활성화 여부


}

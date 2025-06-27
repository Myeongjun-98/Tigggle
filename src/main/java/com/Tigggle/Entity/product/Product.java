package com.Tigggle.Entity.product;

import com.Tigggle.Constant.DataSource;
import com.Tigggle.Constant.ProductType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 일련번호

    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank; // 은행 일련번호

    @Enumerated(EnumType.STRING)
    private ProductType productType; // 상품 타입(예금'deposit', 적금'savings')

    @Enumerated(EnumType.STRING)
    private DataSource dataSource; // 제공받은 데이터('API', 'MANUAL')

    private int periodMonth; // 가입 기간

    private Long amountMoney; // 가입 금액

    private float interestRate; // 상품 금리

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isActive; // 활성화 여부

    private LocalDateTime createdDate; // 상품 등록일

    private LocalDateTime updateDate; // 상품 최종 수정일
}

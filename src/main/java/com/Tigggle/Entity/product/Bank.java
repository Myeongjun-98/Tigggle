package com.Tigggle.Entity.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter

public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 은행 일련번호

    @Column(unique = true)
    private String name; // 은행명

    private String logoUrl; // 은행 로고 url

    private String homepageUrl; // 은행 홈페이지 주소

    // ✅ 상품 리스트 추가 (양방향 매핑)
    @OneToMany(mappedBy = "bank", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

}

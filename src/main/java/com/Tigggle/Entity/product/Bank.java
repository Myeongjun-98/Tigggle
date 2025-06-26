package com.Tigggle.Entity.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 은행 일련번호

    private String name; // 은행명

    private String logoUrl; // 은행 로고 url

    private String homepageUrl; // 은행 홈페이지 주소

}

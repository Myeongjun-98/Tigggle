package com.Tigggle.Entity.news;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter

public class FinancialNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 뉴스 일련번호

    private String newsUrl; // api주소

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isVisible; // 활성화 여부

    private String source; // 출처

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean slide; // 슬라이드 여부

    private String title; // 기사 제목

    private LocalDateTime publicationDate; // 기사 발행일
}

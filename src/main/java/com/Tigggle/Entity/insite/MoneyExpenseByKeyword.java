package com.Tigggle.Entity.insite;

import com.Tigggle.Entity.Transaction.Keywords;
import com.Tigggle.Entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @Entity
public class MoneyExpenseByKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user; //유저

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Keywords keyword; //키워드

    private LocalDate YM; //월
    private Long amount; //금액
}

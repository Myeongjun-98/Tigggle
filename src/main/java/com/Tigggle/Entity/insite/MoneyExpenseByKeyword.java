package com.Tigggle.Entity.insite;

import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.User;

import java.time.LocalDate;

@Getter @Setter
public class MoneyExpenseByKeyword {
    private Long id; //아이디
    private User user; //유저
    private String keyword; //키워드
    private LocalDate YM; //월
    private Long amount; //금액
}

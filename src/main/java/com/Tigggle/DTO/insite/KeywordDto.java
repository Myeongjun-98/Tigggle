package com.Tigggle.DTO.insite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class KeywordDto {

    private String keyword; //키워드이름
    private List<LocalDate> sixMonth; //6달
    private List<Long> sixAmount; //6달 금액
}

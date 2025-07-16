package com.Tigggle.DTO.asset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoalConsumDto {

    private Long foodExpense; //식비
    private Long communication; //주거/통신
    private Long insurance; //보험
    private Long etc; //기타
}

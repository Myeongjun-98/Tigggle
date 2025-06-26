package com.Tigggle.DTO.insite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class SavePayDto {
    private String accountType; //계좌타입
    private String bankLogo; //은행로고
    private String accountAlias; //계좌별칭
    private String amount; //금액
    private int paymentNumber; //납입횟수
    private LocalDate startDate; //시작일
    private LocalDate expirationDate; //만기일
    private Long expectation; //만기시 예상 금액
}

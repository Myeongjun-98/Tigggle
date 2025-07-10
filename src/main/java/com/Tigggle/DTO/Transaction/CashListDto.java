package com.Tigggle.DTO.Transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor @AllArgsConstructor
public class CashListDto {
    private Long cashId;    // (자산 中 현금) 아이디
    private String cashAlias;   // 현금 별칭
}

package com.Tigggle.DTO.Transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class CashDto {
    private String alias;   // 현금 별칭
    private Long balance;   // 현금 잔액
}

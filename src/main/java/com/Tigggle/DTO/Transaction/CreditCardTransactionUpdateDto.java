package com.Tigggle.DTO.Transaction;

import com.Tigggle.Entity.Transaction.Keywords;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor @AllArgsConstructor
public class CreditCardTransactionUpdateDto {
    private String description;     // 내용
    private Keywords keyword;       // 분류
    private String note;            // 메모
}

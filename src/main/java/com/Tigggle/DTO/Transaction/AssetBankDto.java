package com.Tigggle.DTO.Transaction;

import com.Tigggle.Entity.product.Bank;
import lombok.Getter;

@Getter
public class AssetBankDto {

    private Long id;
    private String name;

    public AssetBankDto(Bank bank){
        this.id = bank.getId();
        this.name = bank.getName();
    }

}

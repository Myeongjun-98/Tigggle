package com.Tigggle.DTO.Transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssetCreateDto {

    private String assetType;
    private String alias;
    private Long balance;

    private Long bankId;
    private String accountNumber;

}

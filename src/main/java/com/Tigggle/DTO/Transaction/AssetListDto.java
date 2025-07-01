package com.Tigggle.DTO.Transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AssetListDto {
    private Long assetId;           // 자산 아이디
    private String assetAlias;      // 자산 별칭
}

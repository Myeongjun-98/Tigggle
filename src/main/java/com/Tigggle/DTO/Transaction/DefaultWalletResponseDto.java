package com.Tigggle.DTO.Transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @AllArgsConstructor @NoArgsConstructor @Setter
public class DefaultWalletResponseDto {
    private String status;      // "FOUND" 또는 "NOT_FOUND"
    private Long assetId;       // 찾은 경우의 자산 ID
    private String assetType;   // 찾은 경우의 자산 타입 (예: "CASH", "ORDINARY")
}

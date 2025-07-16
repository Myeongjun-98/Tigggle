package com.Tigggle.DTO.Transaction;

public record AssetSummaryDto(
        Long id,
        String alias,
        String bankName,      // 은행 계좌일 경우
        String accountNumber, // 은행 계좌일 경우
        Long balance
) {
}

package com.Tigggle.DTO.Transaction;

import com.Tigggle.Entity.Member;

public record WalletPageDto(
        String status,                   // "SUCCESS" 또는 "NO_ASSET"
        AssetSummaryDto currentAsset,    // 현재 표시 중인 자산의 요약 정보
        MonthlyLedgerDto monthlyLedger   // 월별 가계부 상세 데이터
) {
}

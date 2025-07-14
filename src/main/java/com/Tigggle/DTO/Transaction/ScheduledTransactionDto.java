package com.Tigggle.DTO.Transaction;

import com.Tigggle.Constant.Transaction.Frequency;
import com.Tigggle.Constant.Transaction.PayMethod;
import com.Tigggle.Entity.Transaction.Keywords;

import java.time.LocalDate;

public record ScheduledTransactionDto(
        Long id,
        String assetType,
        String assetAlias,
        String description,
        String keyword,
        String type,
        Long amount,
        String payMethod,   // ! 이 정기 입출금 관리로는 MYACCOUNT 등으로 자동 거래내역 생성이 안 되도록 한다.
        String note,
        boolean checkingReflection,
        String frequency,
        int dayOfExecution,
        LocalDate nextExecutionDate,
        LocalDate endDate,
        boolean isActive
) {
}

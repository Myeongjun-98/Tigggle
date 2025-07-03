package com.Tigggle.DTO.Transaction;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
//? 일별 거래내역을 담기 위한 DTO
public class DailyLedgerDto {
    private int day;            // 날짜 (ex : 27)
    private String dayOfWeek;   // 요일 (ex : "금")
    private Long dailyTotalIncome;  // 그날의 총 수입
    private Long dailyTotalExpense; // 그날의 총 지출

    // 그날 발생한 모든 거래내역 리스트
    private List<TransactionListDto> transactions;
}

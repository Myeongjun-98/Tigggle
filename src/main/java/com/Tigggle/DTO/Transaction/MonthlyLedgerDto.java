package com.Tigggle.DTO.Transaction;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor @AllArgsConstructor
//? 월별 가계부를 담는 최상위 DTO
public class MonthlyLedgerDto {
    private int year;                   // 조회 연도
    private int month;                  // 조회 월
    private Long monthlyTotalIncome;    // 해당 월의 총수입
    private Long monthlyTotalExpense;   // 해당 월의 총지출

    private List<DailyLedgerDto> dailyLedgers;
}

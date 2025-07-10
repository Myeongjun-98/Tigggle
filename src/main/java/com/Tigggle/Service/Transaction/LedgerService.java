package com.Tigggle.Service.Transaction;

import com.Tigggle.DTO.Transaction.DailyLedgerDto;
import com.Tigggle.DTO.Transaction.MonthlyLedgerDto;
import com.Tigggle.DTO.Transaction.TransactionDetailDto;
import com.Tigggle.DTO.Transaction.TransactionListDto;
import com.Tigggle.Entity.Transaction.Transaction;
import com.Tigggle.Repository.Transaction.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LedgerService {
    private final TransactionRepository transactionRepository;

    private final AssetService assetService;

    // * 월간 사용자의 거래내역을 불러오기!
    public MonthlyLedgerDto getMonthlyLedger(Long assetId, int year, int month) {

        // 1. 조회할 월의 시작일과 종료일 계산
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        LocalDateTime endDate = lastDayOfMonth.atTime(23,59,59);

        // 2. Repository를 통해 해당 기간의 모든 거래내역 조회
        List<Transaction> transactions = transactionRepository.findByAssetIdAndTransactionDateBetween(assetId, startDate, endDate);

        // 3. '날짜'를 기준으로 데이터 그룹핑 (핵심 로직)
        Map<LocalDate, List<Transaction>> groupedByDate = transactions.stream()
                .collect(Collectors.groupingBy(tx -> tx.getTransactionDate().toLocalDate()));

        // 4. 월별 전체 수입/지출 계산
        long monthlyTotalIncome = calculateTotal(transactions, false);
        long monthlyTotalExpense = calculateTotal(transactions, true);

        // 5. 그룹핑된 데이터를 DailyLedgerDto 리스트로 변환
        List<DailyLedgerDto> dailyLedgers = groupedByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder())) // 날짜 내림차순 정렬
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<Transaction> dailyTransactions = entry.getValue();

                    // 일별 수입/지출 계산
                    long dailyIncome = calculateTotal(dailyTransactions, false);
                    long dailyExpense = calculateTotal(dailyTransactions, true);

                    // Transaction 엔티티 리스트를 TransactionListDto 리스트로 변환
                    List<TransactionListDto> transactionDtos = dailyTransactions.stream()
                            .map(tx -> new TransactionListDto
                                    (tx.getId(), tx.isConsumption(), tx.getDescription(), tx.getAmount(), tx.getTransactionDate()))
                            .collect(Collectors.toList());

                    // 최종 DailyLedgerDto 생성
                    return DailyLedgerDto.builder()
                            .day(date.getDayOfMonth())
                            .dayOfWeek(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN))
                            .dailyTotalIncome(dailyIncome)
                            .dailyTotalExpense(dailyExpense)
                            .transactions(transactionDtos)
                            .build();
                })
                .collect(Collectors.toList());

        // 6. 최종 MonthlyLedgerDto 조립 및 반환
        return MonthlyLedgerDto.builder()
                .year(year)
                .month(month)
                .monthlyTotalIncome(monthlyTotalIncome)
                .monthlyTotalExpense(monthlyTotalExpense)
                .dailyLedgers(dailyLedgers)
                .build();
    }

    // * 수입 / 지출 합계 계산 메서드
    private long calculateTotal(List<Transaction> transactions, boolean forConsumption) {
        return transactions.stream()
                .filter(tx -> tx.isConsumption() == forConsumption) // 'isConsumption()' 사용
                .mapToLong(Transaction::getAmount)
                .sum();
    }

}

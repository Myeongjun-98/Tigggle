package com.Tigggle.Service.insite;

import com.Tigggle.DTO.insite.InsiteReponseDto;
import com.Tigggle.DTO.insite.MonthlyDateDto;
import com.Tigggle.Entity.Transaction.Transaction;
import com.Tigggle.Repository.insite.InsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.time.YearMonth.now;

@Service
@RequiredArgsConstructor
public class InsiteService {

    private final InsiteRepository insiteRepository;

    public InsiteReponseDto getSixMonthSpendingSummary(Long memberId, String keyword){
        // 1. 6개월의 시작일과 종료일 계산
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(5).withDayOfMonth(1);

        // 2. Repository를 호출하여 월별 지출 합계 목록을 가져옴
        List<MonthlyDateDto> monthlyDateDtoList = insiteRepository.findMonthlySpendingSummary(
                memberId, startDate, endDate, keyword);

        // 3. 6개월 전체 지출 합계 계산
        Long totalAmount = monthlyDateDtoList.stream()
                .mapToLong(MonthlyDateDto::getTotalAmount)
                .sum();

        // 4. 최종 DTO를 조립하여 반환
        return  new InsiteReponseDto(totalAmount, monthlyDateDtoList);
    }
}

package com.Tigggle.Service.insite;

import com.Tigggle.DTO.insite.InsiteReponseDto;
import com.Tigggle.DTO.insite.KeywordMonthlySpendingDto;
import com.Tigggle.DTO.insite.MonthlyDateDto;
import com.Tigggle.Entity.Transaction.Transaction;
import com.Tigggle.Repository.insite.InsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.time.YearMonth.now;

@Service
@RequiredArgsConstructor
public class InsiteService {

    private final InsiteRepository insiteRepository;
    // *** 전체 소비 총합
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

//        // 4. 최종 DTO를 조립하여 반환
//        return  new InsiteReponseDto(totalAmount, monthlyDateDtoList);



        // db에서 빠질 수 있는 월의 데이터를 보완(0원일 경우를 가정해 보완)
        Map<Integer, Long> monthToAmount  = new HashMap<>();

        for(MonthlyDateDto dto : monthlyDateDtoList){
            // key: 몇 월인지 value:해당 월 소비
            monthToAmount.put(dto.getWhatMonth(), dto.getTotalAmount());
        }

        List<MonthlyDateDto> completedList = IntStream.rangeClosed(0,5)
                .mapToObj(i -> {
                    // 기준 날짜에서 (5 - i)개월 전을 계산하여 실제 월을 구함
                    int month = endDate.minusMonths(5 - i).getMonthValue();

                    //결과 dto 반환
                    // 해당 월의 대한 소비금액 존재 시 사용, 없으면 0L을 기본값으로 사용
                    return new MonthlyDateDto(month, monthToAmount.getOrDefault(month, 0L));

                }).collect(Collectors.toList());

        return new InsiteReponseDto(totalAmount, completedList);
    }


    // 월간 키워드별 소비 요약
    public List<KeywordMonthlySpendingDto> getKeywordMonthlyChart(Long memberId){

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusMonths(5).withDayOfMonth(1);

        List<Object[]> rawData = insiteRepository.getKeywordMonthlyChart(memberId, start, end);

        Map<String, Map<Integer, Long>> KeywordToMonthMap = new HashMap<>();

        for (Object[] row : rawData) {
            String keyword = (String) row[0];
            Integer month = (Integer) row[1];
            Long amount = (Long) row[2];
            KeywordToMonthMap.computeIfAbsent(keyword, k -> new HashMap<>()).put(month, amount);
        }

        List<KeywordMonthlySpendingDto> result = new ArrayList<>();
        for(Map.Entry<String, Map<Integer, Long>> entry : KeywordToMonthMap.entrySet()){
            String keyword = entry.getKey();
            Map<Integer, Long> monthMap = entry.getValue();

            List<Long> amounts = IntStream.rangeClosed(0,5)
                    .mapToObj(i -> {
                        int m = end.minusMonths(5 - i ).getMonthValue();
                        return monthMap.getOrDefault(m, 0L);
                    }).collect(Collectors.toList());

            result.add(new KeywordMonthlySpendingDto(keyword, amounts));
        }

        return result;

    }



}

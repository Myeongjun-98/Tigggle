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
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(5).withDayOfMonth(1);

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
    public List<KeywordMonthlySpendingDto> getKeywordMonthlyChart(Long memberId) {

        LocalDateTime end = LocalDateTime.now();
        // 6개월 전의 1일 00:00:00 시간을 시작점으로 설정
        LocalDateTime start = end.minusMonths(5).withDayOfMonth(1).toLocalDate().atStartOfDay();

        // Repository로부터 raw 데이터를 받아옵니다.
        List<Object[]> rawData = insiteRepository.getKeywordMonthlyChart(memberId, start, end);

        // 1단계: 쿼리 결과를 Map으로 변환합니다.
        // Key: 키워드(String), Value: <월(Integer), 금액(Long)>을 담은 내부 Map
        Map<String, Map<Integer, Long>> keywordToMonthMap = new HashMap<>();

        for (Object[] row : rawData) {
            String keyword = (String) row[0];

            // ▼▼▼▼▼ 바로 이 부분이 핵심 수정사항입니다 ▼▼▼▼▼
            // YEAR()의 결과도 Integer로 받지만, 현재는 사용되지 않습니다.
            // Integer year = (Integer) row[1];
            Integer month = (Integer) row[2]; // MONTH()의 결과는 Integer 입니다.
            Long amount = (Long) row[3];      // SUM()의 결과는 Long 입니다.
            // ▲▲▲▲▲ 바로 이 부분이 핵심 수정사항입니다 ▲▲▲▲▲

            // Map에 데이터를 채워넣습니다.
            keywordToMonthMap.computeIfAbsent(keyword, k -> new HashMap<>()).put(month, amount);
        }

        // 2단계: 변환된 Map을 최종 DTO 리스트로 조립합니다.
        List<KeywordMonthlySpendingDto> result = new ArrayList<>();
        for (Map.Entry<String, Map<Integer, Long>> entry : keywordToMonthMap.entrySet()) {
            String keyword = entry.getKey();
            Map<Integer, Long> monthMap = entry.getValue();

            // 현재로부터 6개월간의 월을 기준으로 금액 리스트를 생성합니다.
            List<Long> amounts = IntStream.rangeClosed(0, 5)
                    .mapToObj(i -> {
                        // end(현재)로부터 5-i 개월 전의 월을 계산합니다.
                        // 예: i=0 -> 5개월 전, i=5 -> 0개월 전(현재 달)
                        int m = end.minusMonths(5 - i).getMonthValue();
                        // 해당 월의 데이터가 없으면 0L을 기본값으로 사용합니다.
                        return monthMap.getOrDefault(m, 0L);
                    })
                    .collect(Collectors.toList());

            result.add(new KeywordMonthlySpendingDto(keyword, amounts));
        }

        return result;
    }



}

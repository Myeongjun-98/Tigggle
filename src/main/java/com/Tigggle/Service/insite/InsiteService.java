package com.Tigggle.Service.insite;

import com.Tigggle.DTO.asset.*;
import com.Tigggle.DTO.insite.*;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.Asset;
import com.Tigggle.Entity.Transaction.BankAccount;
import com.Tigggle.Entity.Transaction.Goal;
import com.Tigggle.Entity.Transaction.Transaction;
import com.Tigggle.Repository.Transaction.AssetRepository;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Repository.insite.GoalRepository;
import com.Tigggle.Repository.insite.InsiteAssetRepository;
import com.Tigggle.Repository.insite.InsiteBankAccountRepository;
import com.Tigggle.Repository.insite.InsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.time.YearMonth.now;

@Service
@RequiredArgsConstructor
public class InsiteService {

    private final InsiteRepository insiteRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final InsiteAssetRepository insiteAssetRepository;
    private final InsiteBankAccountRepository insiteBankAccountRepository;

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

        // 1단계: 쿼리 결과를 Map으로 변환
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



    // 🔹 생년월일로 연령대 문자열 반환
    public String getAgeGroup(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 20) return "10대 이하";
        else if (age < 30) return "20~30대";
        else if (age < 40) return "30~40대";
        else if (age < 50) return "40~50대";
        else if (age < 60) return "50~60대";
        else return "60대 이상";
    }


//    // 나이대별 평균 소비 금액 측정
//    public List<AgeGroupAverageDto> getAgeGroupAverages() {
//        List<Member> allMembers = userRepository.findAll();
//        Map<String, List<Long>> groupToSpending = new HashMap<>();
//
//        for (Member member : allMembers) {
//            String group = getAgeGroup(member.getBirthday());
//            Long sum = insiteRepository.sumAmountByMemberId(member.getId());
//            groupToSpending
//                    .computeIfAbsent(group, k -> new ArrayList<>())
//                    .add(sum != null ? sum : 0L);
//        }
//
//        // 평균 구해서 DTO로 반환
//        return groupToSpending.entrySet().stream()
//                .map(entry -> {
//                    long total = entry.getValue().stream().mapToLong(Long::longValue).sum();
//                    long avg = total / entry.getValue().size();
//                    return new AgeGroupAverageDto(entry.getKey(), avg);
//                })
//                .sorted(Comparator.comparing(AgeGroupAverageDto::getAgeGroup)) // 정렬
//                .collect(Collectors.toList());
//    }

    // 사용자 연령대
    public AgeGroupAverageDto getAgeGroupAverages(String memberAgeRange) {
        List<Member> allMembers = userRepository.findAll();
        Map<String, List<Long>> groupToSpending = new HashMap<>();

        for (Member member : allMembers) {
            String group = getAgeGroup(member.getBirthday());
            Long sum = insiteRepository.sumAmountByMemberId(member.getId());
            groupToSpending
                    .computeIfAbsent(group, k -> new ArrayList<>())
                    .add(sum != null ? sum : 0L);
        }

        // 평균 구해서 DTO로 반환
        List<Long> targetGroupSpending = groupToSpending.getOrDefault(memberAgeRange, new ArrayList<>());

        Long average = 0L;
        if (!targetGroupSpending.isEmpty()) {
            average = targetGroupSpending.stream()
                    .mapToLong(Long::longValue)
                    .sum() / targetGroupSpending.size();
        }

        return new AgeGroupAverageDto(memberAgeRange, average);

    }

    // ** 자산관리**

//    private final AssetRepository assetRepository;


    // 키워드별 한 달 사용금액 표현(식비, 주거/통신, 보험, 기타)
    public AlertDto getKeywordAlert(Long memberId, String keyword, Long goalAmount) {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        int mon = Calendar.getInstance().get(Calendar.MONTH)+1; // 지금 날짜에서 월 추출
        // 이번 달 사용 금액 (keywords 테이블에서 majorKeyword 기준으로 합산)
        Long expenseAmount = insiteRepository.getMonthlySpendingGroupedByMajorKeyword(
                memberId, keyword, mon
        );

        if (expenseAmount == null) expenseAmount = 0L;

        // goalAmount를 받아서 초과금액 계산
        Long excess = Math.max(0, expenseAmount - goalAmount);

        return new AlertDto(keyword, expenseAmount, excess);
    }


    public GoalConsumDto getKeywordSumByMember(Long memberId) {
        // 키워드별로 소비금액을 합산함
        List<Object[]> KeywordSum = insiteRepository.getKeywordSumByMember(memberId);

        // 초기값 설정
        Long food = 0L;
        Long communication = 0L;
        Long insurance = 0L;
        Long etc = 0L;

        // 3. 결과 순회하며 키워드 이름에 따라 분기
        for (Object[] row : KeywordSum) {
            String keyword = (String) row[0];
            Long amount = (Long) row[1];

            switch (keyword.toLowerCase()) {
                case "식비" -> food += amount;
                case "주거/통신" -> communication += amount;
                case "보험" -> insurance += amount;
                default -> etc += amount;
            }
        }

        return new GoalConsumDto(food, communication, insurance, etc);
    }

    // 자산관리 고정 수익 대비 고정 저축
    public FixedAmountDto getFixedAmountDto(Long memberId) {
        List<Goal> goals = goalRepository.findByMemberId(memberId);

        // 초기화
        long incomeSum = 0L;
        long savingSum = 0L;

        for (Goal goal : goals) {
            String major = goal.getKeyword().getMajorKeyword();
            Long amount = goal.getAmount() != null ? goal.getAmount() : 0L;

            if ("급여".equals(major)) {
                incomeSum += amount;
            } else if ("주거/통신비".equals(major) || "저축/보험".equals(major)) {
                savingSum += amount;
            }
        }

        double saveRate = (incomeSum == 0) ? 0.0 : ((incomeSum - savingSum) * 100.0 / incomeSum);

        return new FixedAmountDto(incomeSum, savingSum, saveRate);
    }

    // 이번달 total 구하기
    public MonthTotalDto getMonthTotal(Long memberId) {
        Long total = insiteRepository.getTotalSpent(memberId);
        Long month = insiteRepository.getMonthlySpent(memberId);
        Long today = insiteRepository.getDailySpent(memberId);

        return new MonthTotalDto(
                total != null ? total : 0L,
                month != null ? month : 0L,
                today != null ? today : 0L
        );
    }

    // 예/적금 총합 및 입출금계좌 총합 구하기
    public SavingSummaryDto getSavingSummaryDto(Member member) {
        //예금
        List<BankAccount> deposits = insiteBankAccountRepository.findDepositsByMember(member);
        Long depositSum = deposits.stream()
                .mapToLong(asset -> asset.getBalance() != null ? asset.getBalance() : 0L)
                .sum();

        // 적금
        List<BankAccount> savings = insiteBankAccountRepository.findInstallmentSavingsByMember(member);
        Long savingSum = savings.stream()
                .mapToLong(asset -> asset.getBalance() != null ? asset.getBalance() : 0L)
                .sum();

        // 입출금
        List<BankAccount> accounts = insiteBankAccountRepository.findOrdinaryAccountByMember(member);
        Long accountSum = accounts.stream()
                .mapToLong(asset -> asset.getBalance() != null ? asset.getBalance() : 0L)
                .sum();

        Long total = depositSum + savingSum + accountSum;

        return new SavingSummaryDto(depositSum, savingSum, accountSum, total);

    }

    // 총 목표 소비액 대비 사용금액 계산 로직
    public String getGoalVsSpendingDifference(Long memberId) {
        // 목표 소비 금액
        GoalConsumDto goal = getKeywordSumByMember(memberId);
        long totalGoal = goal.getFoodExpense() + goal.getCommunication()
                + goal.getInsurance() + goal.getEtc();

        // 실제 사용 금액 (이번 달 키워드별 소비 합산)
        Long usedFood = insiteRepository.getMonthlySpendingGroupedByMajorKeyword(memberId, "식비", getCurrentMonth());
        Long usedComm = insiteRepository.getMonthlySpendingGroupedByMajorKeyword(memberId, "주거/통신", getCurrentMonth());
        Long usedIns = insiteRepository.getMonthlySpendingGroupedByMajorKeyword(memberId, "보험", getCurrentMonth());
        Long usedEtc = insiteRepository.getMonthlySpendingGroupedByMajorKeyword(memberId, "기타", getCurrentMonth());

        long totalUsed = Stream.of(usedFood, usedComm, usedIns, usedEtc)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();

        // 차이 계산
        long difference = totalUsed - totalGoal;

        // 결과 문자열 반환
        if (difference > 0) {
            return String.format("%,d원 초과!", difference);
        } else if (difference < 0) {
            return String.format("%,d원 절약!", Math.abs(difference));
        } else {
            return "목표 소비금액과 정확히 일치!";
        }
    }

    // 이번달 숫자 리턴
    private int getCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }

    // 소비습관 카테고리 로직
    public ConsumptionHabitDto getConsumptionHabit(Long memberId) {
        // 1. 목표 금액
        GoalConsumDto goal = getKeywordSumByMember(memberId);  // 식비, 주거/통신, 보험, 기타

        // 2. 실제 사용 금액 (이번 달)
        int month = LocalDate.now().getMonthValue();
        Map<String, Long> actualSpending = new HashMap<>();
        actualSpending.put("식비", safe(insiteRepository.getMonthlySpendingGroupedByMajorKeyword(memberId, "식비", month)));
        actualSpending.put("주거/통신", safe(insiteRepository.getMonthlySpendingGroupedByMajorKeyword(memberId, "주거/통신", month)));
        actualSpending.put("보험", safe(insiteRepository.getMonthlySpendingGroupedByMajorKeyword(memberId, "보험", month)));
        actualSpending.put("기타", safe(insiteRepository.getMonthlySpendingGroupedByMajorKeyword(memberId, "기타", month)));

        // 3. 목표 금액 매핑
        Map<String, Long> goalMap = Map.of(
                "식비", goal.getFoodExpense(),
                "주거/통신", goal.getCommunication(),
                "보험", goal.getInsurance(),
                "기타", goal.getEtc()
        );

        // 4. 계산
        String mostSpent = actualSpending.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("정보 없음");

        String leastSpent = actualSpending.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("정보 없음");

        String mostAligned = actualSpending.entrySet().stream()
                .min(Comparator.comparingLong(e -> Math.abs(e.getValue() - goalMap.getOrDefault(e.getKey(), 0L))))
                .map(Map.Entry::getKey).orElse("정보 없음");

        return new ConsumptionHabitDto(mostSpent, leastSpent, mostAligned);
    }

    private Long safe(Long value) {
        return value != null ? value : 0L;
    }

    // 방법 제시를 위한 총 금액 Long 반환 메서드
    public Long getGoalVsSpendingGapAmount(Long memeberId){
        GoalConsumDto goal = getKeywordSumByMember(memeberId);
        Long goalTotal =  goal.getFoodExpense() + goal.getCommunication()
                + goal.getInsurance() + goal.getEtc();

        // 이번달 지출된~
        Long spentThisMonth = insiteRepository.getMonthlySpent(memeberId);
        if(spentThisMonth == null) spentThisMonth = 0L;

        return Math.abs(spentThisMonth - goalTotal);
    }


    // 방법 제시 로직
    public SavingSuggestionDto getSavingSuggestion(Long memberId) {
        // 1. 목표 소비 금액
        GoalConsumDto goal = getKeywordSumByMember(memberId);
        Long goalTotal = goal.getFoodExpense() + goal.getCommunication() +
                goal.getInsurance() + goal.getEtc();

        // 2. 이번 달 초과 금액
        Long spentThisMonth = insiteRepository.getMonthlySpent(memberId);
        if (spentThisMonth == null) spentThisMonth = 0L;
        Long gap = Math.max(0, spentThisMonth - goalTotal);

        // 3. 다음달 목표 소비액 (기준값)
        Long nextGoal = goalTotal - gap;
        if (nextGoal <= 0) nextGoal = 1L; // 0으로 나누는 것 방지

        // 4. 항목별 계산
        double americano = nextGoal / 5_000.0;
        double alcohol = nextGoal / 50_000.0;
        double travel = nextGoal / 500_000.0;
        double luxury = nextGoal / 5_000_000.0;

        return new SavingSuggestionDto(
                Math.round(americano * 10000) / 10000.0,
                Math.round(alcohol * 10000) / 10000.0,
                Math.round(travel * 10000) / 10000.0,
                Math.round(luxury * 1000000) / 1000000.0
        );
    }

}

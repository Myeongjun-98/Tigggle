package com.Tigggle.Service.Transaction;

import com.Tigggle.Constant.Transaction.Frequency;
import com.Tigggle.Constant.Transaction.PayMethod;
import com.Tigggle.DTO.Transaction.ScheduledTransactionCreateDto;
import com.Tigggle.DTO.Transaction.ScheduledTransactionDto;
import com.Tigggle.DTO.Transaction.ScheduledTransactionUpdateDto;
import com.Tigggle.DTO.Transaction.TransactionCreateRequestDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.*;
import com.Tigggle.Repository.Transaction.AssetRepository;
import com.Tigggle.Repository.Transaction.KeywordsRepository;
import com.Tigggle.Repository.Transaction.ScheduledTransactionRepository;
import com.Tigggle.Repository.Transaction.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduledTransactionService {

    private final ScheduledTransactionRepository scheduledTransactionRepository;
    private final TransactionRepository transactionRepository;
    private final AssetRepository  assetRepository;
    private final TransactionService transactionService;
    private final KeywordsRepository keywordsRepository;

    public List<ScheduledTransactionDto> getScheduledTransactions(Member member) {
        // 1. Repository를 통해 사용자의 모든 정기 거래 엔티티를 조회합니다.
        List<ScheduledTransaction> schedules = scheduledTransactionRepository.findByMember(member);

        // 2. 조회된 엔티티 리스트를 DTO 리스트로 변환합니다.
        return schedules.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 엔티티를 DTO로 변환하는 헬퍼 메소드
    private ScheduledTransactionDto convertToDto(ScheduledTransaction schedule) {

        if (schedule == null) {
            return null;
        }

        ScheduledTransactionDto dto = new ScheduledTransactionDto();

        // 공통 필드 채우기
        dto.setId(schedule.getId());
        dto.setDescription(schedule.getDescription());
        dto.setAmount(schedule.getAmount());
        dto.setNote(schedule.getNote());
        dto.setStartDate(schedule.getStartDate());
        dto.setEndDate(schedule.getEndDate());
        dto.setDayOfExecution(schedule.getDayOfExecution());

        // 목록 표시용 필드 채우기
        dto.setType(schedule.isConsumption() ? "지출" : "수입");
        dto.setFrequency(schedule.getFrequency().name());
        dto.setAssetAlias(schedule.getAsset().getAlias());
        dto.setKeyword(schedule.getKeyword().getMajorKeyword() + " > " + schedule.getKeyword().getMinorKeyword());

        // 수정 및 로직용 필드 채우기
        dto.setAssetId(schedule.getAsset().getId());
        dto.setKeywordId(schedule.getKeyword().getId());
        dto.setActive(schedule.isActive());
        dto.setReflectOnAsset(schedule.isReflectOnAsset());
        dto.setNextExecutionDate(schedule.getNextExecutionDate());
        dto.setConsumption(schedule.isConsumption());

        return dto;
    }



    // * 정기 입출금 내역 생성
    @Transactional // 데이터를 생성하므로 @Transactional은 필수입니다.
    public void createScheduledTransaction(ScheduledTransactionCreateDto createDto, Member member) {

        // 1. DTO에 담긴 ID로 관련 엔티티들을 조회합니다.
        Asset asset = (Asset)assetRepository.findByIdAndMember(createDto.getAssetId(), member)
                .orElseThrow(() -> new SecurityException("자산에 대한 권한이 없습니다."));

        Keywords keyword = keywordsRepository.findById(createDto.getKeywordId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 키워드입니다."));

        // 2. 새로운 ScheduledTransaction 엔티티 객체를 생성합니다.
        ScheduledTransaction schedule = new ScheduledTransaction();

        // 3. DTO의 값들로 엔티티의 필드를 채웁니다.
        schedule.setAsset(asset);
        schedule.setKeyword(keyword);
        schedule.setDescription(createDto.getDescription());
        schedule.setAmount(createDto.getAmount());
        schedule.setConsumption(createDto.isConsumption());
        schedule.setPayMethod(createDto.getPayMethod());
        schedule.setNote(createDto.getNote());

        schedule.setReflectOnAsset(createDto.isReflectOnAsset());

        schedule.setFrequency(createDto.getFrequency());
        schedule.setDayOfExecution(createDto.getDayOfExecution());
        LocalDate startDate = LocalDate.now();
        schedule.setStartDate(startDate);
        schedule.setEndDate(createDto.getEndDate());
        schedule.setActive(true); // 새로 생성하는 규칙은 기본적으로 활성화 상태

        // 4. (핵심 로직) 첫 번째 '다음 실행일'을 계산합니다.
        LocalDate firstExecutionDate = calculateNextExecutionDate(
                startDate,
                createDto.getFrequency(),
                createDto.getDayOfExecution()
        );
        schedule.setNextExecutionDate(firstExecutionDate);

        // 5. 완성된 엔티티를 데이터베이스에 저장합니다.
        scheduledTransactionRepository.save(schedule);
    }

    /**
     * 시작일과 반복 규칙을 바탕으로, 첫 번째 '다음 실행일'을 계산하는 헬퍼 메소드
     */
    private LocalDate calculateNextExecutionDate(LocalDate startDate, Frequency frequency, int dayOfExecution) {

        switch (frequency) {
            case MONTHLY:
                // 1. 해당 월의 실행일로 날짜를 설정합니다.
                LocalDate monthlyNextDate = startDate.withDayOfMonth(dayOfExecution);
                // 2. 만약 시작일이 이미 해당 월의 실행일보다 늦다면, 다음 달로 설정합니다.
                if (monthlyNextDate.isBefore(startDate)) {
                    return monthlyNextDate.plusMonths(1);
                }
                return monthlyNextDate;

            case WEEKLY:
                // 1. 현재 요일과 목표 요일의 차이를 계산합니다.
                int currentDayOfWeek = startDate.getDayOfWeek().getValue(); // 월요일(1) ~ 일요일(7)
                int daysToAdd = (dayOfExecution - currentDayOfWeek + 7) % 7;
                // 2. 시작일에 계산된 날짜를 더합니다.
                return startDate.plusDays(daysToAdd);

//            case YEARLY:
//                // 1. 올해의 해당 날짜를 계산합니다. (시작일의 월/일을 사용)
//                LocalDate yearlyNextDate = startDate.withYear(startDate.getYear());
//                // 2. 만약 올해의 해당 날짜가 이미 지났다면, 내년으로 설정합니다.
//                if (yearlyNextDate.isBefore(startDate)) {
//                    return yearlyNextDate.plusYears(1);
//                }
//                return yearlyNextDate;

            default:
                // 다른 주기가 추가될 경우를 대비
                return startDate;
        }
    }

    // * 정기 내역 삭제
    @Transactional
    public void deleteScheduledTransactions(List<Long> scheduleIds, Member member) {

        // 1. Repository에서 삭제할 모든 엔티티를 한 번에 조회합니다.
        List<ScheduledTransaction> schedulesToDelete =
                scheduledTransactionRepository.findAllByIdInAndAsset_Member(scheduleIds, member);

        // 2. 요청된 ID 개수와 실제 조회된 개수가 다르면, 권한이 없거나 존재하지 않는 ID가 포함된 것이므로 예외를 발생시킵니다.
        if (schedulesToDelete.size() != scheduleIds.size()) {
            throw new SecurityException("삭제할 권한이 없거나, 존재하지 않는 항목이 포함되어 있습니다.");
        }

        // 3. 조회된 모든 엔티티를 한 번에 삭제합니다.
        scheduledTransactionRepository.deleteAll(schedulesToDelete);
    }

    @Transactional
    public void updateScheduledTransaction(Long scheduleId, ScheduledTransactionUpdateDto updateDto, Member member) {
        // 1. 소유권 검증과 함께 수정할 엔티티를 조회합니다.
        ScheduledTransaction schedule = scheduledTransactionRepository.findByIdAndAsset_Member(scheduleId, member)
                .orElseThrow(() -> new SecurityException("수정할 권한이 없는 정기 거래입니다."));

        Keywords keyword = keywordsRepository.findById(updateDto.getKeywordId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 분류입니다."));

        // 2. DTO에 담긴 새로운 값으로 엔티티의 필드를 업데이트합니다.
        schedule.setDescription(updateDto.getDescription());
        schedule.setAmount(updateDto.getAmount());
        schedule.setKeyword(keyword); // 찾은 Keyword 객체로 설정
        schedule.setNote(updateDto.getNote());
        schedule.setEndDate(updateDto.getEndDate());
        schedule.setReflectOnAsset(updateDto.isReflectOnAsset());
        schedule.setActive(updateDto.isActive());
        schedule.setFrequency(updateDto.getFrequency());
        schedule.setDayOfExecution(updateDto.getDayOfExecution());
        schedule.setConsumption(updateDto.isConsumption());
        }

    public ScheduledTransactionDto getSingleScheduledTransaction(Long scheduleId) {
        ScheduledTransaction schedule = scheduledTransactionRepository.findByIdWithDetails(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 정기 거래입니다."));

        // 기존의 변환 메소드를 재사용합니다.
        return convertToDto(schedule);
    }

    // * 정기 스케쥴러 실행
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void executeScheduledTransactions(){
        System.out.println("정기 거래 스케쥴러 실행: " + LocalDateTime.now());

        LocalDate today = LocalDate.now();

        int dayOfMonth = today.getDayOfMonth();
        int dayOfWeek = today.getDayOfWeek().getValue();
        // 1. 오늘 실행되어야 할 모든 정기 거래 목록을 DB에서 조회합니다.
        List<ScheduledTransaction> dueSchedules = scheduledTransactionRepository.findSchedulesDueOn(dayOfMonth, dayOfWeek);

        // 2. 각 정기 거래 규칙에 대해 실제 거래내역(Transaction)을 생성합니다.
        for (ScheduledTransaction schedule : dueSchedules) {
            // DTO를 생성하여 TransactionService에 전달
            TransactionCreateRequestDto createDto = new TransactionCreateRequestDto();
            createDto.setTransactionType(schedule.isConsumption() ? "EXPENSE" : "INCOME");
            createDto.setDescription(schedule.getDescription());
            createDto.setAmount(schedule.getAmount());
            createDto.setTransactionDate(LocalDateTime.now());
            createDto.setKeywordId(schedule.getKeyword().getId());
            createDto.setNote("정기 거래로 자동 생성됨");
            createDto.setPayMethod("SCHEDULED");

            // Asset에서 Member 정보를 가져옵니다.
            Member member = schedule.getAsset().getMember();

            // sourceAssetId, creditCardId 등 payMethod에 따라 추가 설정...
            if ("CREDIT_CARD".equals(createDto.getPayMethod())) {
                createDto.setCreditCardId(schedule.getAsset().getId());
            } else {
                createDto.setSourceAssetId(schedule.getAsset().getId());
            }

            // TransactionService의 생성 로직을 호출하여 실제 거래를 생성합니다.
            transactionService.createTransaction(createDto, member);
        }
    }

}

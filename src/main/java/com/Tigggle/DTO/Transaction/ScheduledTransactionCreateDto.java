package com.Tigggle.DTO.Transaction;

import com.Tigggle.Constant.Transaction.Frequency;
import com.Tigggle.Constant.Transaction.PayMethod;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTransactionCreateDto {

    // 1. 어떤 자산에 대한 규칙인가?
    private Long assetId;

    // 2. 거래의 기본 정보
    private String description;
    private Long amount;
    private boolean isConsumption; // 지출 여부 (true/false)

    // 3. 지출일 경우에만 필요한 정보
    private PayMethod payMethod; // Enum 타입 그대로 사용

    // 4. 분류 및 메모
    private Long keywordId;
    private String note;

    private boolean reflectOnAsset;

    // 5. 정기 거래 규칙 정보
    private Frequency frequency; // 반복 주기 (MONTHLY, WEEKLY 등)
    private int dayOfExecution;  // 실행일 (예: 25일, 또는 요일 숫자)
    private LocalDate endDate;     // 종료일 (선택사항이므로 null일 수 있음)

}

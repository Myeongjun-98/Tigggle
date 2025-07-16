package com.Tigggle.DTO.Transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class ScheduledTransactionDto {
    // ==== 목록/수정 공통 필드 ====
    private Long id;
    private String description;
    private Long amount;
    private String note;
    private LocalDate startDate;
    private LocalDate endDate;
    private int dayOfExecution;

    // ==== 목록 표시용 필드 ====
    private String type;         // "지출" 또는 "수입"
    private String frequency;    // "매월", "매주" 등
    private String assetAlias;   // 자산 별칭
    private String keyword;      // 키워드 이름

    // ==== 수정 창 및 내부 로직용 필드 ====
    private Long assetId;        // 자산 원본 ID
    private Long keywordId;      // 키워드 원본 ID

    @JsonProperty("isActive")
    private boolean isActive;    // 활성 여부 (true/false)
    private boolean reflectOnAsset; // 자산 반영 여부 (true/false)
    private LocalDate nextExecutionDate;

    @JsonProperty("isConsumption")
    private boolean isConsumption; // 지출 여부 (true/false)
}

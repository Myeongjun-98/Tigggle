package com.Tigggle.DTO.Transaction;

import java.time.LocalDate;

import com.Tigggle.Constant.Transaction.Frequency;
import com.Tigggle.Constant.Transaction.PayMethod;
import com.Tigggle.Entity.Transaction.Keywords;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor @AllArgsConstructor
public class ScheduledTransactionUpdateDto {
    private String description;
    private Long amount;
    private Long keywordId; // keyword_id cannot be null 오류 해결
    private String note;
    private LocalDate endDate;
    private boolean reflectOnAsset;
    private boolean isActive;
    private Frequency frequency;
    private int dayOfExecution;

    @JsonProperty("isConsumption")
    private boolean isConsumption;
}

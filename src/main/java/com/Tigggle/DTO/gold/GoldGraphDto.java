package com.Tigggle.DTO.gold;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter

public class GoldGraphDto {

    private List<GoldListDto> goldPrices;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}

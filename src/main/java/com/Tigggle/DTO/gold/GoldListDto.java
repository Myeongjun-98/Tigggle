package com.Tigggle.DTO.gold;

import com.Tigggle.Entity.gold.Gold;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class GoldListDto {
    private Long id;
    private Long price;
    private String priceUnit;
    private LocalDateTime changeDate;

    public static GoldListDto from(Gold gold) {

        GoldListDto goldListDto = new GoldListDto();

        goldListDto.setId(gold.getId());
        goldListDto.setPrice((long)Math.round(gold.getPrice()));
        goldListDto.setPriceUnit(gold.getPriceUnit());
        goldListDto.setChangeDate(gold.getChangeDate());

        return goldListDto;
    }
}

package com.Tigggle.DTO.news;

import com.Tigggle.Entity.news.FinancialNews;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class FinancialNewsDto {

    private Long id;
    private String title;
    private String newsUrl;

    public static FinancialNewsDto from(FinancialNews financialNews) {

        FinancialNewsDto financialNewsDto = new FinancialNewsDto();

        financialNewsDto.setId(financialNews.getId());
        financialNewsDto.setTitle(financialNews.getTitle());
        financialNewsDto.setNewsUrl(financialNews.getNewsUrl());

        return financialNewsDto;
    }
}

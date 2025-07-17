package com.Tigggle.DTO.community;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PageInfoDto {
    private int startPage;
    private int endPage;
    private boolean hasPrev;
    private boolean hasNext;
    private int totalPages;
    private int currentPage;
}

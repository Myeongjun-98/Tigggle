package com.Tigggle.DTO.community;

import com.Tigggle.Constant.CommunityCategory;
import com.Tigggle.Constant.SearchType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunitySearchDto {

    private SearchType searchType;

    private String keyWord;

    private CommunityCategory category;
}

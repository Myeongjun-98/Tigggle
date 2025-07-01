package com.Tigggle.DTO.community;

import com.Tigggle.Constant.Community.CommunityCategory;
import com.Tigggle.Constant.Community.SearchType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunitySearchDto {

    private SearchType searchType;

    private String keyWord;

    private CommunityCategory category;
}

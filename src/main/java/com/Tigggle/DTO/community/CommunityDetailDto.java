package com.Tigggle.DTO.community;

import com.Tigggle.Constant.CommunityCategory;
import com.Tigggle.Entity.community.CommunityBoard;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter

public class CommunityDetailDto {
    private Long id;
    private String title;
    private String content;
    private CommunityCategory category;
    private String name;
    private LocalDateTime writeDate;
    private LocalDateTime updateDate;
    private List<CommunityCommentDto> comments;
    private List<CommunityImgDto> images;
    private List<CommunityGraphDto> graphs;

    public static CommunityDetailDto from(CommunityBoard communityBoard,
                                          List<CommunityCommentDto> communityCommentsDtos,
                                          List<CommunityImgDto> communityImgDtos,
                                          List<CommunityGraphDto> communityGraphDtos) {
        CommunityDetailDto communityDetailDto = new CommunityDetailDto();
        communityDetailDto.setId(communityBoard.getId());
        communityDetailDto.setTitle(communityBoard.getTitle());
        communityDetailDto.setContent(communityBoard.getContent());
        communityDetailDto.setCategory(communityBoard.getCommunityCategory());
        communityDetailDto.setName(communityBoard.getMember().getName());
        communityDetailDto.setWriteDate(communityBoard.getWriteDate());
        communityDetailDto.setUpdateDate(communityBoard.getUpdateDate());
        communityDetailDto.setComments(communityCommentsDtos);
        communityDetailDto.setImages(communityImgDtos);
        communityDetailDto.setGraphs(communityGraphDtos);

        return communityDetailDto;
    }
}

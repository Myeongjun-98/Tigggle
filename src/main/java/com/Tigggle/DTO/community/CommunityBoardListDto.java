package com.Tigggle.DTO.community;

import com.Tigggle.Constant.CommunityCategory;
import com.Tigggle.Entity.community.CommunityBoard;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Locale;

@Getter
@Setter

public class CommunityBoardListDto {
    private Long id;
    private String title;
    private CommunityCategory category;
    private String nickName;
    private LocalDateTime writeDate;
    private int commentCount;
    private int hit;
    private Boolean deleted;

    public static CommunityBoardListDto from(
            CommunityBoard communityBoard, int commentCount) {
            CommunityBoardListDto communityBoardListDto = new CommunityBoardListDto();
            communityBoardListDto.setId(communityBoard.getId());
            communityBoardListDto.setTitle(communityBoard.getTitle());
            communityBoardListDto.setCategory(communityBoard.getCommunityCategory());
            communityBoardListDto.setNickName(communityBoard.getUser().getNickName());
            communityBoardListDto.setWriteDate(communityBoard.getWriteDate());
            communityBoardListDto.setCommentCount(commentCount);
            communityBoardListDto.setHit(communityBoard.getHit());
            communityBoardListDto.setDeleted(communityBoard.isDeleted());

        return communityBoardListDto;
    }
}

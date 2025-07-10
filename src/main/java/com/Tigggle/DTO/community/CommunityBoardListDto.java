package com.Tigggle.DTO.community;

import com.Tigggle.Constant.Community.CommunityCategory;
import com.Tigggle.Entity.community.CommunityBoard;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter

public class CommunityBoardListDto {
    private Long id;
    private String title;
    private CommunityCategory category;
    private String name;
    private LocalDateTime writeDate;
    private int commentCount;
    private int hit;
    private boolean hasImage;
    private boolean hasGraph;
    private boolean deleted;
    private String formattedWriteDate;

    public static CommunityBoardListDto from(
            CommunityBoard communityBoard, int commentCount, boolean hasImage, boolean hasGraph) {

            CommunityBoardListDto communityBoardListDto = new CommunityBoardListDto();

            communityBoardListDto.setId(communityBoard.getId());
            communityBoardListDto.setTitle(communityBoard.getTitle());
            communityBoardListDto.setCategory(communityBoard.getCommunityCategory());
            communityBoardListDto.setName(communityBoard.getMember().getName());
            communityBoardListDto.setWriteDate(communityBoard.getWriteDate());
            communityBoardListDto.setCommentCount(commentCount);
            communityBoardListDto.setHit(communityBoard.getHit());
            communityBoardListDto.setHasImage(hasImage);
            communityBoardListDto.setHasGraph(hasGraph);
            communityBoardListDto.setDeleted(communityBoard.isDeleted());

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime writeDate = communityBoard.getWriteDate();

            if(writeDate.toLocalDate().isEqual(now.toLocalDate())) {
                communityBoardListDto
                        .setFormattedWriteDate(writeDate.format(DateTimeFormatter.ofPattern("HH:mm")));
            } else {
                communityBoardListDto
                        .setFormattedWriteDate(writeDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }

        return communityBoardListDto;
    }

    public static CommunityBoardListDto createCommunityBoardListDto(CommunityBoard communityBoard) {
        CommunityBoardListDto communityBoardListDto = new CommunityBoardListDto();
        communityBoardListDto.setId(communityBoard.getId());
        communityBoardListDto.setTitle(communityBoard.getTitle());
        communityBoardListDto.setCategory(communityBoard.getCommunityCategory());
        communityBoardListDto.setName(communityBoard.getMember().getName());
        communityBoardListDto.setWriteDate(communityBoard.getWriteDate());
        return communityBoardListDto;
    }
}

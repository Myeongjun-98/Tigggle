package com.Tigggle.DTO.community;

import com.Tigggle.Constant.Community.CommunityCategory;
import com.Tigggle.Entity.community.CommunityBoardGraph;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class CommunityGraphDto {

    private Long id;
    private CommunityCategory communityCategory;
    private Long communityBoardId;
    private LocalDateTime startDate;
    private LocalDateTime finishDate;

    public static CommunityGraphDto from(CommunityBoardGraph communityBoardGraph) {

        CommunityGraphDto communityGraphDto = new CommunityGraphDto();

        communityGraphDto.setId(communityBoardGraph.getId());
        communityGraphDto.setCommunityCategory(communityBoardGraph.getCommunityBoard().getCommunityCategory());
        communityGraphDto.setCommunityBoardId(communityBoardGraph.getId());
        communityGraphDto.setStartDate(communityBoardGraph.getStartDate());
        communityGraphDto.setFinishDate(communityBoardGraph.getFinishDate());

        return communityGraphDto;

    }
}

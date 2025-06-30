package com.Tigggle.DTO.community;

import com.Tigggle.Constant.CommunityCategory;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.community.CommunityBoard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CommunityWriteDto {

    private CommunityCategory category;

    private String title;

    private String content;

    public CommunityBoard to(Member member) {
        CommunityBoard communityBoard = new CommunityBoard();
        communityBoard.setTitle(this.title);
        communityBoard.setContent(this.content);
        communityBoard.setMember(member);

        return communityBoard;
    }
}

package com.Tigggle.DTO.community;

import com.Tigggle.Constant.CommunityCategory;
import com.Tigggle.Entity.User;
import com.Tigggle.Entity.community.CommunityBoard;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

@Getter
@Setter

public class CommunityWriteDto {

    private CommunityCategory category;

    private String title;

    private String content;

    public CommunityBoard to(User user) {
        CommunityBoard communityBoard = new CommunityBoard();
        communityBoard.setTitle(this.title);
        communityBoard.setContent(this.content);

        return communityBoard;
    }
}

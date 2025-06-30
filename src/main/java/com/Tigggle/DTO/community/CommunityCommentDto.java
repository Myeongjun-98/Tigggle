package com.Tigggle.DTO.community;

import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.community.CommunityBoard;
import com.Tigggle.Entity.community.CommunityComment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class CommunityCommentDto {

    private Long id;
    private String content;
    private String name;
    private LocalDateTime writeDate;
    private LocalDateTime updateDate;
    private Boolean deleted;

    public CommunityComment to (Member member, CommunityBoard communityBoard) {

        CommunityComment communityComment = new CommunityComment();
        communityComment.setContent(this.content);
        communityComment.setMember(member);
        communityComment.setCommunityBoard(communityBoard);
        communityComment.setWriteDate(this.writeDate);
        communityComment.setUpdateDate(this.updateDate);
        communityComment.setDeleted(false);

        return communityComment;
    }

    public static CommunityCommentDto from(CommunityComment communityComment) {

        CommunityCommentDto communityCommentDto = new CommunityCommentDto();

        communityCommentDto.setId(communityComment.getId());
        communityCommentDto.setContent(communityComment.getContent());
        communityCommentDto.setName(communityComment.getMember().getName());
        communityCommentDto.setWriteDate(communityComment.getWriteDate());
        communityCommentDto.setUpdateDate(communityComment.getUpdateDate());
        communityCommentDto.setDeleted(communityComment.isDeleted());
        return communityCommentDto;

    }
}

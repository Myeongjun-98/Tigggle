package com.Tigggle.DTO.community;

import com.Tigggle.Constant.Community.CommunityCategory;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.community.CommunityBoard;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter

public class CommunityWriteDto {

    private CommunityCategory category;

    private String title;

    private String content;

    private List<MultipartFile> images; // 이미지 업로드용

    // 그래프 기간
    private LocalDate startDate;
    private LocalDate finishDate;

    public CommunityBoard to(Member member) {
        CommunityBoard communityBoard = new CommunityBoard();

        communityBoard.setTitle(this.title);
        communityBoard.setContent(this.content);
        communityBoard.setMember(member);
        communityBoard.setCommunityCategory(this.category);

        return communityBoard;
    }
}

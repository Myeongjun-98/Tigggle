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
    private List<Long> deleteImageIds;
    private List<CommunityImgDto> existingImages; // 기본이미지 정보

    private List<CommunityGraphDto> graphs; // 그래프 기간들

    public CommunityBoard to(Member member) {
        CommunityBoard communityBoard = new CommunityBoard();

        communityBoard.setTitle(this.title);
        communityBoard.setContent(this.content);
        communityBoard.setMember(member);
        communityBoard.setCommunityCategory(this.category);

        return communityBoard;
    }

    public static CommunityWriteDto from(CommunityDetailDto communityDetailDto) {

        CommunityWriteDto communityWriteDto = new CommunityWriteDto();

        communityWriteDto.setExistingImages(communityDetailDto.getImages());
        communityWriteDto.setTitle(communityDetailDto.getTitle());
        communityWriteDto.setContent(communityDetailDto.getContent());
        communityWriteDto.setCategory(communityDetailDto.getCategory());

        communityWriteDto.setGraphs(communityDetailDto.getGraphs());
        return communityWriteDto;
    }
}

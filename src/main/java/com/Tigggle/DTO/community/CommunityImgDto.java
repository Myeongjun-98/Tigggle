package com.Tigggle.DTO.community;

import com.Tigggle.Entity.community.CommunityBoard;
import com.Tigggle.Entity.community.CommunityBoardImage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CommunityImgDto {

    private Long id;
    private Long communityBoardId;
    private String originalName;
    private String imgName;
    private String imgUrl;

    public CommunityBoardImage to(CommunityBoard communityBoard) {

        CommunityBoardImage communityBoardImage = new CommunityBoardImage();
        communityBoardImage.setCommunityBoard(communityBoard);
        communityBoardImage.setOriginalName(this.originalName);
        communityBoardImage.setImgName(this.imgName);
        communityBoardImage.setImgUrl(this.imgUrl);

        return communityBoardImage;
    }
}

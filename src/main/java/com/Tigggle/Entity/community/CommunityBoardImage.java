package com.Tigggle.Entity.community;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

public class CommunityBoardImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 게시글 사진 일련번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_board_id")
    private CommunityBoard communityBoard; // 게시글 일련번호

    private String originalName; // 사용자가 업로드 할때 파일명

    private String imgName; // DB에 저장되는 파일명

    private String imgUrl; // 이미지 주소
}

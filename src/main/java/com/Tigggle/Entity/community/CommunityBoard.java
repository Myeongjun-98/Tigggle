package com.Tigggle.Entity.community;

import com.Tigggle.Constant.CommunityCategory;
import com.Tigggle.Entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter

public class CommunityBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 게시글 일련번호

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; // 사용자

    private String title; // 제목

    @Lob
    private String content; // 내용

    @Enumerated(EnumType.STRING)
    private CommunityCategory communityCategory; // 게시글 카테고리

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int hit; // 조회수

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean deleted; // 삭제 여부

    private LocalDateTime writeDate; // 작성일

    private LocalDateTime updateDate; // 수정일

    private LocalDateTime deletedDate; // 삭제일

}

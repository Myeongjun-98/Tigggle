package com.Tigggle.Entity.community;

import com.Tigggle.Entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;


@Entity
@Getter
@Setter

public class CommunityComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 댓글 일련번호

    @ManyToOne
    @JoinColumn(name = "community_board_id")
    private CommunityBoard communityBoard; // 커뮤니티 게시글 일련번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 댓글 작성자

    private String content; // 댓글 내용

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean deleted; // 댓글 삭제 여부

    private LocalDateTime writeDate; // 작성일

    private LocalDateTime updateDate; // 수정일

    private LocalDateTime deletedDate; // 삭제일
}

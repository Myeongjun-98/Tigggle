package com.Tigggle.Entity.community;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter

public class CommunityBoardGraph {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 그래프 일련번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_board_id")
    private CommunityBoard communityBoard; // 커뮤니티 게시글 일련번호

    private LocalDateTime startdate; // 그래프 날짜설정(시작일)

    private LocalDateTime finishDate; // 그래프 날짜설정(마지막날짜)
}

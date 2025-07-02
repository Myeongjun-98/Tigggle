package com.Tigggle.Service.community;

import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.community.CommunityBoard;
import com.Tigggle.Entity.community.CommunityComment;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Repository.community.CommunityBoardRepository;
import com.Tigggle.Repository.community.CommunityCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommunityCommentService {

    private final CommunityBoardRepository communityBoardRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final UserRepository userRepository;

    public void addComment(Long boardId, String userName, String content) {

        Member member = userRepository.findByAccessId(userName);
        if(member == null) {
            throw new RuntimeException("사용자 없음");
        }

        CommunityBoard communityBoard = communityBoardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        CommunityComment communityComment = new CommunityComment();
        communityComment.setCommunityBoard(communityBoard);
        communityComment.setMember(member);
        communityComment.setContent(content);
        communityComment.setWriteDate(LocalDateTime.now());
        communityComment.setDeleted(false);

        communityCommentRepository.save(communityComment);
    }
}

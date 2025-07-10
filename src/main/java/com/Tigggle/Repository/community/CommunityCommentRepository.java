package com.Tigggle.Repository.community;

import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.community.CommunityBoard;
import com.Tigggle.Entity.community.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    List<CommunityComment> findByMember(Member member); // 로그인 유저가 작성한 댓글 만 가져오기

    int countByCommunityBoardIdAndDeletedFalse(Long communityBoardId);

    List<CommunityComment> findByCommunityBoardId(Long boardId);

    List<CommunityComment> findByCommunityBoardAndDeletedFalse(CommunityBoard communityBoard);
}

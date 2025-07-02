package com.Tigggle.Repository.community;

import com.Tigggle.Entity.community.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    int countByCommunityBoardIdAndDeletedFalse(Long communityBoardId);

    List<CommunityComment> findByCommunityBoardId(Long boardId);
}

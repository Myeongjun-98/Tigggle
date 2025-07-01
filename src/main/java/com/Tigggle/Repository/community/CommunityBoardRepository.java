package com.Tigggle.Repository.community;

import com.Tigggle.Entity.community.CommunityBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface CommunityBoardRepository extends JpaRepository<CommunityBoard, Long> {
    // List<CommunityBoard> findCommunityCategoryAndDeletedFalseAndOrderByWriteDateDesc();
}

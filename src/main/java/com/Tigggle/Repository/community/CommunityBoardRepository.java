package com.Tigggle.Repository.community;

import com.Tigggle.Constant.Community.CommunityCategory;
import com.Tigggle.Entity.community.CommunityBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface CommunityBoardRepository extends JpaRepository<CommunityBoard, Long> {
    List<CommunityBoard> findByCommunityCategoryAndDeletedIsFalseOrderByWriteDateDesc(CommunityCategory communityCategory);

    Optional<CommunityBoard> findByIdAndDeletedFalse(Long id);
}

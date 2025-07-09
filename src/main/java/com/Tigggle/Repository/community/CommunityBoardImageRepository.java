package com.Tigggle.Repository.community;

import com.Tigggle.Entity.community.CommunityBoardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityBoardImageRepository extends JpaRepository<CommunityBoardImage, Long> {

    boolean existsByCommunityBoardId(Long communityBoardId);

    List<CommunityBoardImage> findByCommunityBoardId(Long boardId);
}

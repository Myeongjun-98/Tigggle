package com.Tigggle.Repository.community;

import com.Tigggle.Entity.community.CommunityBoardGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityBoardGraphRepository extends JpaRepository<CommunityBoardGraph, Long> {

    boolean existsByCommunityBoardId(Long communityBoardId);

    List<CommunityBoardGraph> findByCommunityBoardId(Long boardId);
}

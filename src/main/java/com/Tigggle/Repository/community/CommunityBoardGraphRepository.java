package com.Tigggle.Repository.community;

import com.Tigggle.Entity.community.CommunityBoardGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityBoardGraphRepository extends JpaRepository<CommunityBoardGraph, Long> {
}

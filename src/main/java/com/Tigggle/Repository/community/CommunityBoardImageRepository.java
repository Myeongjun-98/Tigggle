package com.Tigggle.Repository.community;

import com.Tigggle.Entity.community.CommunityBoardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityBoardImageRepository extends JpaRepository<CommunityBoardImage, Long> {
}

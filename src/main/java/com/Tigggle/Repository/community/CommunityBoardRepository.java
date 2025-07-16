package com.Tigggle.Repository.community;

import com.Tigggle.Constant.Community.CommunityCategory;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.community.CommunityBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository

public interface CommunityBoardRepository extends JpaRepository<CommunityBoard, Long> {

    List<CommunityBoard> findByMember(Member member, Pageable pageable); // 로그인 유저의 게시글 만 가져오기



    Page<CommunityBoard> findByCommunityCategoryAndDeletedIsFalseOrderByWriteDateDesc(
            CommunityCategory communityCategory, Pageable pageable);

    Optional<CommunityBoard> findByIdAndDeletedFalse(Long id);

    // 제목 검색
    Page<CommunityBoard> findByCommunityCategoryAndTitleContainingAndDeletedIsFalseOrderByWriteDateDesc(
            CommunityCategory category, String keyword, Pageable pageable);

    // 작성자 검색
    Page<CommunityBoard> findByCommunityCategoryAndMemberNameContainingAndDeletedIsFalseOrderByWriteDateDesc(
            CommunityCategory category, String keyword, Pageable pageable);
}

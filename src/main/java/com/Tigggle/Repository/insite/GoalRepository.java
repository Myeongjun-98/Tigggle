package com.Tigggle.Repository.insite;

import com.Tigggle.Entity.Transaction.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    // 특정 회원의 Goal 리스트를 모두 가져오기
    List<Goal> findByMemberId(Long memberId);

}


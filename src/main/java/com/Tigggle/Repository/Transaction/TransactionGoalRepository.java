package com.Tigggle.Repository.Transaction;

import com.Tigggle.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.Goal;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionGoalRepository extends JpaRepository<Goal, Long>{
    // 현재 로그인한 사용자의 모든 목표를 조회하기 위한 메서드
    List<Goal> findAllByMember(Member member);

    // ID와 사용자 정보를 함께 사용하여, 해당 사용자의 목표가 맞는지 확인하며 조회하기 위한 메서드
    Optional<Goal> findByIdAndMember(Long id, Member member);

    // 여러 ID와 사용자 정보를 사용하여, 해당 사용자의 목표들만 안전하게 삭제하기 위한 메서드
    void deleteAllByIdInAndMember(List<Long> ids, Member member);
}

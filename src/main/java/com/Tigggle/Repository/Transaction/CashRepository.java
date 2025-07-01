package com.Tigggle.Repository.Transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.Cash;

@Repository
public interface CashRepository extends JpaRepository<Cash, Long>{
    
    // 현금계좌 정보 가져오기
    @Query("SELECT a FROM ASSET a WHERE a.member.id = :memberID AND TYPE(a) IN (Cash)")
    List<Cash> cashList(@Param("memberId") Long memberId);
}

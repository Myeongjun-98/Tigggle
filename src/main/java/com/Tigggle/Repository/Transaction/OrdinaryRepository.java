package com.Tigggle.Repository.Transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.OrdinaryAccount;

@Repository
public interface OrdinaryRepository extends JpaRepository<OrdinaryAccount, Long>{
    
    // 보통예금계좌 정보 가져오기
    @Query("SELECT a FROM Asset a WHERE a.member.id = :memberId AND TYPE(a) IN (OrdinaryAccount)")
    List<OrdinaryAccount> OrdinaryAccountList(@Param("memberId") Long memberId);

    OrdinaryAccount findByMemberId(Long memberId);

}

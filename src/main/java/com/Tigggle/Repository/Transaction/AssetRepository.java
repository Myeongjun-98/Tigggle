package com.Tigggle.Repository.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.Asset;

import java.util.List;
import com.Tigggle.Entity.Member;


@Repository
public interface AssetRepository extends JpaRepository<Asset, Long>{

    // 사용자의 아이디로 모든 자산정보 가져오기
    public List<Asset> findAllById(Long memberId);

    // 사용자의 아이디로 모든 계좌정보(개설일 기준) 가져오기
    // JPQL의 TYPE연산자로
    @Query("SELECT a FROM Asset a WHERE a.member.id = :memberId AND TYPE(a) IN (Cash, OrdinaryAccount)")
    List<Asset> findCashAndOrdinaryAssetsByMemberId(@Param("memberId") Long memberId);

}
package com.Tigggle.Repository.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.Asset;
import com.Tigggle.Entity.Transaction.Cash;
import com.Tigggle.Entity.Transaction.OrdinaryAccount;

import java.util.List;

import com.Tigggle.DTO.Transaction.AssetListDto;


@Repository
public interface AssetRepository extends JpaRepository<Asset, Long>{

    // 사용자의 아이디로 모든 자산정보 가져오기
    public List<Asset> findAllById(Long memberId);

    @Query("SELECT a FROM Asset a WHERE a.member.id = :memberAccessId AND TYPE(a) IN (Cash, OrdinaryAccount)")
    List<Asset> findCashAndOrdinaryAssetsByMemberId(@Param("memberAccessId") Long memberId);

}

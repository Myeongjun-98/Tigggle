package com.Tigggle.Repository.Transaction;

import com.Tigggle.Entity.Member;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.Asset;

import java.util.List;
import java.util.Optional;


@Repository
public interface AssetRepository extends JpaRepository<Asset, Long>{

    // 사용자의 아이디로 모든 지갑자산정보 가져오기
    public List<Asset> findAllByMember(Member member);

    @Query("SELECT a FROM Asset a WHERE a.member.id = :memberId AND TYPE(a) IN (Cash, OrdinaryAccount)")
    List<Asset> findCashAndOrdinaryAssetsByMemberId(@Param("memberId") Long memberId);

    Optional<Object> findByIdAndMember(Long sourceAssetId, Member member);

    /**
     * ID와 사용자 ID로 자산을 조회하면서, 해당 데이터 행에 쓰기 잠금(Pessimistic Write Lock)을 설정합니다.
     * 다른 트랜잭션은 이 잠금이 해제될 때까지 해당 행에 접근할 수 없습니다. (데드락 방지)
     * @param assetId 자산 ID
     * @param memberId 사용자 ID
     * @return 잠금이 설정된 Asset 객체
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Asset a WHERE a.id = :assetId AND a.member.id = :memberId")
    Optional<Asset> findByIdAndMemberWithLock(@Param("assetId") Long assetId, @Param("memberId") Long memberId);

    // 거래내역 지웠을 시 잔액 다시 되돌리는 쿼리문
    @Query(value = "select balance from asset where id= :assetId" ,nativeQuery = true)
    public Long findBalanceById(@Param("assetId") Long assetId);

    // 계산한 잔액을 통째로 바꿔끼우는 쿼리문
    @Modifying
    @Query(value = "update asset set balance= :val where id= :assetId" ,nativeQuery = true)
    public void updateBalance( @Param("val") Long val,@Param("assetId") Long assetId);
}

package com.Tigggle.Repository.Transaction;

import com.Tigggle.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{

    @Query("SELECT t FROM Transaction t WHERE t.asset.id = :assetId AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<Transaction> findByAssetIdAndDateRange(
            @Param("assetId") Long assetId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    List<Transaction> findByAssetIdAndTransactionDateBetween(Long assetId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * transactionId와 Member 객체를 사용하여, 해당 사용자의 소유가 맞는 거래내역을 조회합니다.
     * @param transactionId 조회할 거래내역의 ID
     * @param member 현재 로그인한 사용자의 Member 엔티티
     * @return 조건에 맞는 거래내역 (Optional)
     */
    @Query("SELECT t FROM Transaction t WHERE t.id = :transactionId AND t.asset.member = :member")
    Optional<Transaction> findByIdAndMember(
            @Param("transactionId") Long transactionId,
            @Param("member") Member member
    );

}

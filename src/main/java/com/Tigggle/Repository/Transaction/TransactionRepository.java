package com.Tigggle.Repository.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.Transaction;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{

    @Query("SELECT t FROM Transaction t WHERE t.asset.id = :assetId AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<Transaction> findByAssetIdAndDateRange(
            @Param("assetId") Long assetId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    List<Transaction> findByAssetIdAndTransactionDateBetween(Long assetId, LocalDateTime startDate, LocalDateTime endDate);
}

package com.Tigggle.Repository.insite;


import com.Tigggle.DTO.insite.MonthlyDateDto;
import com.Tigggle.Entity.Transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InsiteRepository extends JpaRepository<Transaction, Long> {

//    // transacion에서 사용자의 "is_consumption =true, reflect_on_asset=true" 인것 가져오기
//    List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);

    // 소비내역 (월) 다더하기

//    @Query(value = """
//    SELECT MONTH(t.transaction_date) AS month, SUM(t.amount) AS total_amount
//    FROM transaction t
//    WHERE t.is_consumption = true
//      AND t.reflect_on_asset = true
//    GROUP BY MONTH(t.transaction_date)
//    ORDER BY month
//    """, nativeQuery = true)
//    List<Object[]> getMonthlyTotalAmount();


    // JPQL의 new 키워드와 GROUP BY를 사용하여 월별 지출 합계를 바로 DTO로 조회
    @Query("SELECT new com.Tigggle.DTO.insite.MonthlyDateDto(MONTH(t.transactionDate), SUM(t.amount)) " + // DTO 패키지명 수정
            "FROM Transaction t " +
            "WHERE t.asset.member.id = :memberId " +
            "AND t.isConsumption = true " + // 지출 내역만
            "AND t.transactionDate BETWEEN :startDate AND :endDate " +
            "AND (LOWER(t.keyword.majorKeyword) = LOWER(:keyword) OR :keyword IS NULL) " + // 키워드 필터링: minorKeyword -> MajorKeyword 수정
            "GROUP BY YEAR(t.transactionDate), MONTH(t.transactionDate) " +
            "ORDER BY YEAR(t.transactionDate), MONTH(t.transactionDate)")

    List<MonthlyDateDto> findMonthlySpendingSummary(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("keyword") String keyword);



    @Query("SELECT k.majorKeyword, YEAR(t.transactionDate), MONTH(t.transactionDate), SUM(t.amount) " + // YEAR() 추가
            "FROM Transaction t JOIN t.keyword k JOIN t.asset a " +
            "WHERE a.member.id = :memberId " +
            "AND t.transactionDate BETWEEN :start AND :end " +
            "AND t.isConsumption = true " +
            "AND t.reflectOnAsset = true " +
            "GROUP BY k.majorKeyword, YEAR(t.transactionDate), MONTH(t.transactionDate)") // YEAR() 추가
    List<Object[]> getKeywordMonthlyChart(@Param("memberId") Long memberId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);


    // 나이대별(6구간) 소비 습관 측정
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.asset.member.id = :memberId AND t.isConsumption = true AND t.reflectOnAsset = true")
    Long sumAmountByMemberId(@Param("memberId") Long memberId);




//---------------------------------------------------------------------------------------

    // ** 자산관리 **
    @Query("SELECT k.majorKeyword, SUM(t.amount) " +
            "FROM Transaction t JOIN t.keyword k " +
            "WHERE t.asset.member.id = :memberId " +
            "AND t.isConsumption = true " +
            "AND t.reflectOnAsset = true " +
            "GROUP BY k.majorKeyword")
    List<Object[]> getKeywordSumByMember(@Param("memberId") Long memberId);

    // 결과는 Object[] 형식으로, row[0] = 키워드, row[1] = 합계입니다.


}

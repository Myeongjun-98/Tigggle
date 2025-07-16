package com.Tigggle.Repository.Transaction;

import com.Tigggle.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.ScheduledTransaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledTransactionRepository extends JpaRepository<ScheduledTransaction, Long>{

    @Query("SELECT s FROM ScheduledTransaction s WHERE s.asset.member = :member")
    public List<ScheduledTransaction> findByMember(@Param("member") Member member);

    // 오늘 날짜(dayOfMonth)와 요일(dayOfWeek)에 맞는 활성화된 정기 거래를 조회하는 쿼리
    @Query("SELECT s FROM ScheduledTransaction s WHERE s.isActive = true AND " +
            "(s.frequency = 'MONTHLY' AND s.dayOfExecution = :dayOfMonth) OR " +
            "(s.frequency = 'WEEKLY' AND s.dayOfExecution = :dayOfWeek)")
    List<ScheduledTransaction> findSchedulesDueOn(
            @Param("dayOfMonth") int dayOfMonth,
            @Param("dayOfWeek") int dayOfWeek
    );

    List<ScheduledTransaction> findAllByIdInAndAsset_Member(List<Long> scheduleIds, Member member);

    Optional<ScheduledTransaction> findByIdAndAsset_Member(Long scheduleId, Member member);

    @Query("SELECT s FROM ScheduledTransaction s " +
            "JOIN FETCH s.asset a " +
            "JOIN FETCH s.keyword k " +
            "WHERE s.id = :scheduleId")
    Optional<ScheduledTransaction> findByIdWithDetails(@Param("scheduleId") Long scheduleId);

}

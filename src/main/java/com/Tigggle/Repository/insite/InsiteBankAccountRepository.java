package com.Tigggle.Repository.insite;

import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.Asset;
import com.Tigggle.Entity.Transaction.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsiteBankAccountRepository extends JpaRepository<BankAccount, Long> {

    @Query("SELECT a FROM Asset a WHERE a.member = :member AND TYPE(a) IN (InstallmentSaving , Deposit)")
    List<BankAccount> findInstallmentSavingsAndDepositsByMember(@Param("member") Member member);

    // 예금 총금액 구하는 쿼리
    // 가 아니라 일단 가져와서 서비스에서 다더해서 구할거임

    @Query("SELECT a FROM Asset a WHERE a.member = :member AND TYPE(a) IN (InstallmentSaving)")
    List<BankAccount> findInstallmentSavingsByMember(@Param("member") Member member);

    // 적금 총금액 구하는 쿼리
    // 가 아니라 일단 가져와서 서비스에서 다더해서 구할거임
    @Query("SELECT a FROM Asset a WHERE a.member = :member AND TYPE(a) IN (Deposit)")
    List<BankAccount> findDepositsByMember(@Param("member") Member member);
    // 입출금 ?????
    @Query("SELECT a FROM Asset a WHERE a.member = :member AND TYPE(a) IN (OrdinaryAccount)")
    List<BankAccount> findOrdinaryAccountByMember(@Param("member") Member member);
}

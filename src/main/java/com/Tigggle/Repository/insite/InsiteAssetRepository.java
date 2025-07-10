package com.Tigggle.Repository.insite;

import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsiteAssetRepository extends JpaRepository<Asset,Long> {

    @Query("SELECT a FROM Asset a WHERE a.member = :memberId AND TYPE(a) IN (InstallmentSaving , Deposit)")
    List<Asset> findInstallmentSavingsAndDepositsByMemberId(@Param("member") Member member);

}

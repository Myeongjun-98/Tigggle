package com.Tigggle.Repository.Transaction;

import java.util.List;

import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.Cash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.OrdinaryAccount;

@Repository
public interface OrdinaryRepository extends JpaRepository<OrdinaryAccount, Long>{

    @Query("SELECT o FROM OrdinaryAccount o WHERE o.member = :member")
    List<OrdinaryAccount> findByMember(@Param("member") Member member);

    @Query("SELECT o FROM OrdinaryAccount o WHERE o.id = :assetId AND TYPE(o) IN (OrdinaryAccount)")
    OrdinaryAccount singleOrdinary(@Param("assetId") Long assetId);

}

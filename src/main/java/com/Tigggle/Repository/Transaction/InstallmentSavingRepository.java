package com.Tigggle.Repository.Transaction;

import com.Tigggle.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.InstallmentSaving;

import java.util.List;

@Repository
public interface InstallmentSavingRepository extends JpaRepository<InstallmentSaving, Long>{

    @Query("SELECT i FROM InstallmentSaving i WHERE i.member = :member")
    List<InstallmentSaving> findByMember(Member member);
}

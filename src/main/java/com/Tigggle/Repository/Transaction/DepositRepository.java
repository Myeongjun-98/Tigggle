package com.Tigggle.Repository.Transaction;

import com.Tigggle.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.Deposit;

import java.util.List;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long>{

    @Query("SELECT d FROM Deposit d WHERE d.member = :member")
    List<Deposit> findByMember(Member member);


}

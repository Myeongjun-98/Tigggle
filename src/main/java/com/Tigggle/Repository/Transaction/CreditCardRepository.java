package com.Tigggle.Repository.Transaction;

import com.Tigggle.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.CreditCard;

import java.util.List;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long>{

    @Query("SELECT c FROM CreditCard c WHERE c.member = :member")
    List<CreditCard> findByMember(@Param("member") Member member);
}

package com.Tigggle.Repository.Transaction;

import com.Tigggle.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.CreditCard;

import java.util.List;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long>{

    List<CreditCard> findByMember(Member member);
}

package com.Tigggle.Repository.Transaction;

import com.Tigggle.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.CheckCard;

import java.util.List;

@Repository
public interface CheckCardRepository extends JpaRepository<CheckCard, Long>{

    List<CheckCard> findByMember(Member member);
}

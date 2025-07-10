package com.Tigggle.Repository.Transaction;

import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Object> findByIdAndMember(Long creditCardId, Member member);


}

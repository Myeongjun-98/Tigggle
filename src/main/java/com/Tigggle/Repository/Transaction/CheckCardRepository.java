package com.Tigggle.Repository.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.CheckCard;

@Repository
public interface CheckCardRepository extends JpaRepository<CheckCard, Long>{

}

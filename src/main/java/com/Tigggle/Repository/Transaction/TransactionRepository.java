package com.Tigggle.Repository.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{

}

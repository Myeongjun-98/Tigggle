package com.Tigggle.Repository.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.ScheduledTransaction;

@Repository
public interface ScheduledTransactionRepository extends JpaRepository<ScheduledTransaction, Long>{

}

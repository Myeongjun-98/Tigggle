package com.Tigggle.Repository.Transaction;

import com.Tigggle.Entity.Transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.CreditCardTransaction;

import java.util.List;

@Repository
public interface CreditCardTransactionRepository extends JpaRepository<CreditCardTransaction, Long>{

}

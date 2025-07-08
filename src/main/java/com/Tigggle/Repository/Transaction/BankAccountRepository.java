package com.Tigggle.Repository.Transaction;

import com.Tigggle.Entity.Transaction.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

}

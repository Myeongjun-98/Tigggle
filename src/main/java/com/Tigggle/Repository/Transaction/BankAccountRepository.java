package com.Tigggle.Repository.Transaction;

import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    List<BankAccount> findByMember(Member member);
}

package com.Tigggle.Repository.Transaction;

import com.Tigggle.Entity.Transaction.CreditCardInstallmentPayment;
import com.Tigggle.Entity.Transaction.CreditCardTransaction;
import com.Tigggle.Entity.Transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditCardInstallmentPaymentRepository extends JpaRepository<CreditCardInstallmentPayment, Long> {
    void deleteByOriginalTransaction(CreditCardInstallmentPayment cardTx);

    List<CreditCardInstallmentPayment> findByOriginalTransaction(Transaction transaction);
}

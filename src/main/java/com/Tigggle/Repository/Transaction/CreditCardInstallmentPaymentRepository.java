package com.Tigggle.Repository.Transaction;

import com.Tigggle.Entity.Transaction.CreditCardInstallmentPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditCardInstallmentPaymentRepository extends JpaRepository<CreditCardInstallmentPayment, Long> {
}

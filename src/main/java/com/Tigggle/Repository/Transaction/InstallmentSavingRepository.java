package com.Tigggle.Repository.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.InstallmentSaving;

@Repository
public interface InstallmentSavingRepository extends JpaRepository<InstallmentSaving, Long>{

}

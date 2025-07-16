package com.Tigggle.Repository.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.Keywords;

@Repository
public interface KeywordsRepository extends JpaRepository<Keywords, Long>{

}

package com.Tigggle.Repository.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.Goal;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long>{

}

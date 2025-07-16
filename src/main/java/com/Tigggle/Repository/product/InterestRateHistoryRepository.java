package com.Tigggle.Repository.product;

import com.Tigggle.Entity.product.InterestRateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface InterestRateHistoryRepository extends JpaRepository<InterestRateHistory, Long> {
}

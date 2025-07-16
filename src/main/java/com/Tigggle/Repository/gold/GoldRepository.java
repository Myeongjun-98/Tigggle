package com.Tigggle.Repository.gold;

import com.Tigggle.Entity.gold.Gold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoldRepository extends JpaRepository<Gold, Long> {

}

package com.Tigggle.Repository.Transaction;

import java.util.List;

import com.Tigggle.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.OrdinaryAccount;

@Repository
public interface OrdinaryRepository extends JpaRepository<OrdinaryAccount, Long>{

    List<OrdinaryAccount> findByMember(Member member);
}

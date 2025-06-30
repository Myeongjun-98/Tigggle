package com.Tigggle.Repository;


import com.Tigggle.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Member, Long> {
    Member findByAccessId(String accessId);  //  로그인 및 그외 용 인데.....

}

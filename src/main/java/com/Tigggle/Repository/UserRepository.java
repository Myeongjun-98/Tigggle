package com.Tigggle.Repository;


import com.Tigggle.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByAccessId(String accessId);  //  로그인 및 그외 용 인데.....

}

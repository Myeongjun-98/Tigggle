package com.Tigggle.Service;

import com.Tigggle.Entity.Member;
import com.Tigggle.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByAccessId(username);
        if(member == null) {
            throw new UsernameNotFoundException(username);
        }

        return User.builder()
                .username(member.getAccessId())
                .password(member.getPassword())
                .roles(member.getRole().toString()).build();
    }

}

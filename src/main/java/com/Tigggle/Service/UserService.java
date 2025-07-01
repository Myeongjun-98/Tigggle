package com.Tigggle.Service;

import com.Tigggle.DTO.MemberFormDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = userRepository.findByAccessId(username);
        if(member == null) {
            throw new UsernameNotFoundException(username);
        }

        return User.builder()
                .username(member.getAccessId())
                .password(member.getPassword())
                .roles(member.getRole().toString()).build();
    }

    public void signUpSave(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        Member member = memberFormDto.createMember(passwordEncoder);
        userRepository.save(member);
    }

    public boolean idCheck(String id) { // 아이디 중복 체크

        return false;
    }
}

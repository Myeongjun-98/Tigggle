package com.Tigggle.DTO;

import com.Tigggle.Constant.Role;
import com.Tigggle.Constant.UserStatus;
import com.Tigggle.Entity.Member;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Data
public class MemberFormDto {

    private String accessId;  // 로그인 아이디

    private String password;  // 비밀번호

    private String name;  //이름

    private String tel;  //연락처

    private boolean gender; //성별

    private LocalDate birthday;   //생년월일


    public Member createMember(PasswordEncoder passwordEncoder) {

        Member member = new Member();
        member.setAccessId(accessId);
        String encodedPassword = passwordEncoder.encode(password);
        member.setPassword(encodedPassword);
        member.setName(name);
        member.setTel(tel);
        member.setGender(gender);
        member.setBirthday(birthday);
        member.setRole(Role.USER);
        member.setUserStatus(UserStatus.JOIN);

        return member;
    }
}

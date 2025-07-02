package com.Tigggle.DTO;

import com.Tigggle.Constant.Role;
import com.Tigggle.Constant.UserStatus;
import com.Tigggle.Entity.Member;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Data
public class MemberFormDto {
    @NotNull(message = "아이디는 필수입니다.")
    @Pattern(
            regexp = "^[a-z\\d]{6,20}$",
            message = "아이디는 6~20자, 영소문자와 숫자만 사용 가능합니다."
    )
    private String accessId;  // 로그인 아이디

    @NotNull(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d]{8,16}$",
            message = "비밀번호는 8~16자, 대문자·소문자·숫자 조합이어야 합니다."
    )
    private String password;  // 비밀번호

    @NotNull(message = "이름은 필수입니다.")
    @Pattern(
            regexp = "^[가-힣]+$",
            message = "이름은 한글만 입력 가능합니다."
    )
    private String name;  //이름

    @NotNull(message = "연락처는 필수 입니다.")
    @Pattern(
            regexp = "^\\d{10,11}$",
            message = "연락처는 숫자만 10자리 또는 11자리로 입력해야 합니다."
    )
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

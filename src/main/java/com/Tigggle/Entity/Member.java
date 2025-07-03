package com.Tigggle.Entity;

import com.Tigggle.Constant.Role;
import com.Tigggle.Constant.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String accessId;  // 로그인 아이디
    @Column(nullable = false)
    private String password;  // 비밀번호
    @Column(nullable = false)
    private String name;  //이름
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String tel;  //연락처
    @Column(nullable = false)
    private boolean gender; //성별
    @Column(nullable = false)
    private LocalDate birthday;   //생년월일
    private byte counselingTokken=3;
    @Column(nullable = false)
    private String profileImage="/image/defProfile.jpg";  // 프로필이미지
    @Enumerated(EnumType.STRING)
    private Role role; // 권한
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus; // 유저 상태 - 가입, 탈퇴, 탈퇴대기


}

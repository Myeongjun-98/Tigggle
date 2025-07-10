package com.Tigggle.DTO;

import com.Tigggle.DTO.community.CommunityBoardListDto;
import com.Tigggle.DTO.community.CommunityCommentDto;
import com.Tigggle.Entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class MyPageDto {
    private String accessId;  // 로그인 아이디
    private String tel;  //연락처
    private String name;
    private String email;
    private boolean gender; //성별
    private String profileUrl;
    private LocalDate birthday;   //생년월일
    private byte counselingTokken;

    private List<CommunityBoardListDto> communityBoardList;
    private List<CommunityCommentDto> communityCommentDtoList;

    public static MyPageDto createMyPageDto(Member member,List<CommunityBoardListDto> communityBoardList,List<CommunityCommentDto> communityCommentDtoList ) {
        MyPageDto myPageDto = new MyPageDto();

        myPageDto.accessId = member.getAccessId();
        myPageDto.tel = member.getTel();
        myPageDto.email = member.getEmail();
        myPageDto.name = member.getName();
        myPageDto.profileUrl=member.getProfileImage();
        myPageDto.setGender(member.isGender());
        myPageDto.counselingTokken=member.getCounselingTokken();
        myPageDto.birthday = member.getBirthday();
        myPageDto.communityBoardList = communityBoardList;
        myPageDto.communityCommentDtoList = communityCommentDtoList;

        return myPageDto;
    }
}

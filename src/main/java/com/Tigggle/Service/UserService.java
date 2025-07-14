package com.Tigggle.Service;

import com.Tigggle.DTO.MemberFormDto;
import com.Tigggle.DTO.MyPageDto;
import com.Tigggle.DTO.community.CommunityBoardListDto;
import com.Tigggle.DTO.community.CommunityCommentDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.community.CommunityBoard;
import com.Tigggle.Entity.community.CommunityComment;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Repository.community.CommunityBoardRepository;
import com.Tigggle.Repository.community.CommunityCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final CommunityBoardRepository communityBoardRepository;
    private final CommunityCommentRepository communityCommentRepository;


    @Value("${uploadPath}")
    private String uploadPath;


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


    //  사용처 - 마이페이지 정보 주기( userController)
    public MyPageDto userInfo(String name, Pageable pageable) {
        Member member = userRepository.findByAccessId(name);
        List<CommunityBoard> communityBoards = communityBoardRepository.findByMember(member,pageable);
        List<CommunityComment> communityComments = communityCommentRepository.findByMember(member,pageable);

        List<CommunityBoardListDto> communityBoardListDtos = new ArrayList<>();
        List<CommunityCommentDto> communityCommentDtos = new ArrayList<>();

        for(CommunityBoard communityBoard : communityBoards) {
            CommunityBoardListDto communityBoardListDto =  CommunityBoardListDto.createCommunityBoardListDto(communityBoard);
            communityBoardListDto.setCommentCount(communityCommentRepository.countByCommunityBoardIdAndDeletedFalse(communityBoard.getId()));
            communityBoardListDtos.add(communityBoardListDto);
        }
        for(CommunityComment communityComment : communityComments) {
            communityCommentDtos.add( CommunityCommentDto.from(communityComment) );
        }


        MyPageDto myPageDto = MyPageDto.createMyPageDto(member, communityBoardListDtos,communityCommentDtos);

        return myPageDto;
    }


    // 프로필 이미지 변경 -업로드 처리
    public String saveProfileImage(MultipartFile file, String name) throws IOException {
        // 고유 파일명 생성
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // 저장 경로 설정
        File dest = new File(uploadPath, fileName);
        file.transferTo(dest);
    System.out.println("aaaaaaaaaa");
        // DB에 저장
        String imageUrl = "/uploads/" + fileName;  // 정적 경로 기반
        Member member = userRepository.findByAccessId(name);
        member.setProfileImage(imageUrl);
        //userRepository.save(member);

        return imageUrl;
    }
    @Transactional
    public void updateUserInfo(String userId, MyPageDto dto) {
        Member member = userRepository.findByAccessId(userId);
        member.myInfoUpdate(dto);
        userRepository.save(member);
    }
}

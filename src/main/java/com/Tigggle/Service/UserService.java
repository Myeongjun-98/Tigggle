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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final CommunityBoardRepository communityBoardRepository;
    private final CommunityCommentRepository communityCommentRepository;

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

    public MyPageDto userInfo(String name) {
        Member member = userRepository.findByAccessId(name);
        List<CommunityBoard> communityBoards = communityBoardRepository.findByMember(member);
        List<CommunityComment> communityComments = communityCommentRepository.findByMember(member);

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
}

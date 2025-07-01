package com.Tigggle.Service.community;

import com.Tigggle.DTO.community.CommunityBoardListDto;
import com.Tigggle.Entity.community.CommunityBoard;
import com.Tigggle.Repository.community.CommunityBoardRepository;
import com.Tigggle.Repository.community.CommunityCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor

public class CommunityBoardService {

    private final CommunityBoardRepository communityBoardRepository;

    private final CommunityCommentRepository communityCommentRepository;

    public List<CommunityBoardListDto> getCommunityBoards() {

        List<CommunityBoardListDto> communityBoardListDtos = new ArrayList<>();

        List<CommunityBoard> communityBoards = communityBoardRepository.
                findCommunityCategoryAndDeletedFalseOrderByWriteDateDesc();

        for (CommunityBoard communityBoard : communityBoards) {

            int commentCount = communityCommentRepository
                    .countByCommunityBoardIdAndDeletedFalse(communityBoard.getId());

            CommunityBoardListDto communityBoardListDto
                    = CommunityBoardListDto.from(communityBoard, commentCount);

            communityBoardListDtos.add(communityBoardListDto);
        }

        return communityBoardListDtos;

    }

}


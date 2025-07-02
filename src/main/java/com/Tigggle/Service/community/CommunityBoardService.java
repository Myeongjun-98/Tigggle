package com.Tigggle.Service.community;

import com.Tigggle.Constant.Community.CommunityCategory;
import com.Tigggle.DTO.community.*;
import com.Tigggle.Entity.community.CommunityBoard;
import com.Tigggle.Entity.community.CommunityComment;
import com.Tigggle.Repository.community.CommunityBoardGraphRepository;
import com.Tigggle.Repository.community.CommunityBoardImageRepository;
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
    private final CommunityBoardImageRepository communityBoardImageRepository;
    private final CommunityBoardGraphRepository communityBoardGraphRepository;

    public List<CommunityBoardListDto> getCommunityBoards(CommunityCategory communityCategory) {

        List<CommunityBoardListDto> communityBoardListDtos = new ArrayList<>();

        List<CommunityBoard> communityBoards = communityBoardRepository.
                findByCommunityCategoryAndDeletedIsFalseOrderByWriteDateDesc(communityCategory);

        for (CommunityBoard communityBoard : communityBoards) {

            int commentCount = communityCommentRepository
                    .countByCommunityBoardIdAndDeletedFalse(communityBoard.getId());

            boolean hasImage = communityBoardImageRepository
                    .existsByCommunityBoardId(communityBoard.getId());
            boolean hasGraph = communityBoardGraphRepository
                    .existsByCommunityBoardId(communityBoard.getId());

            CommunityBoardListDto communityBoardListDto
                    = CommunityBoardListDto.from(communityBoard, commentCount, hasImage, hasGraph);

            communityBoardListDtos.add(communityBoardListDto);
        }

        return communityBoardListDtos;

    }

    public CommunityDetailDto getBoardDetail(Long id) {

        CommunityBoard communityBoard = communityBoardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        List<CommunityCommentDto> comments = communityCommentRepository
                .findByCommunityBoardId(id).stream()
                .map(CommunityCommentDto::from)
                .toList();

        List<CommunityImgDto> images = communityBoardImageRepository
                .findByCommunityBoardId(id).stream()
                .map(CommunityImgDto::from)
                .toList();

        List<CommunityGraphDto> graphs = communityBoardGraphRepository
                .findByCommunityBoardId(id).stream()
                .map(CommunityGraphDto::from)
                .toList();


        return CommunityDetailDto.from(communityBoard, comments, images, graphs);
    }

    public List<CommunityBoardListDto> getTipBoards() {

        return getCommunityBoards(CommunityCategory.TIP);
    }

    public List<CommunityBoardListDto> getDiscussionBoards() {

        return getCommunityBoards(CommunityCategory.DISCUSSION);
    }

    public List<CommunityBoardListDto> getEconomicMarketBoards() {

        return getCommunityBoards(CommunityCategory.ECONOMIC_MARKET);
    }

}


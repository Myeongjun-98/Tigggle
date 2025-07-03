package com.Tigggle.Service.community;

import com.Tigggle.Constant.Community.CommunityCategory;
import com.Tigggle.DTO.community.*;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.community.CommunityBoard;
import com.Tigggle.Entity.community.CommunityBoardGraph;
import com.Tigggle.Entity.community.CommunityBoardImage;
import com.Tigggle.Entity.community.CommunityComment;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Repository.community.CommunityBoardGraphRepository;
import com.Tigggle.Repository.community.CommunityBoardImageRepository;
import com.Tigggle.Repository.community.CommunityBoardRepository;
import com.Tigggle.Repository.community.CommunityCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class CommunityBoardService {

    private final CommunityBoardRepository communityBoardRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityBoardImageRepository communityBoardImageRepository;
    private final CommunityBoardGraphRepository communityBoardGraphRepository;
    private final UserRepository userRepository;

    private final String uploadDir = "C:/community/";

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

    public Long saveTipBoard(CommunityWriteDto communityWriteDto,
                                          String userAccessId) {

        // 사용자 조회
        Member member = userRepository .findByAccessId(userAccessId);
        if(member == null) throw new RuntimeException("사용자 없음");

        // 게시글 생성 및 저장
        CommunityBoard communityBoard = communityWriteDto.to(member);
        communityBoard.setWriteDate(LocalDateTime.now());
        communityBoard.setCommunityCategory(communityWriteDto.getCategory());
        communityBoardRepository.save(communityBoard);

        // 이미지 저장
        if(communityWriteDto.getImages() != null) {
            for (MultipartFile file : communityWriteDto.getImages()) {
                if(!file.isEmpty()) {
                    try {
                        String originalName = file.getOriginalFilename();
                        String uuid = UUID.randomUUID().toString();
                        String extension = originalName.substring(
                                originalName.lastIndexOf("."));
                        String savedName = uuid + extension;
                        String fullPath = uploadDir + savedName;

                        file.transferTo(new File(fullPath));

                        CommunityBoardImage communityBoardImage = new CommunityBoardImage();
                        communityBoardImage.setCommunityBoard(communityBoard);
                        communityBoardImage.setOriginalName(originalName);
                        communityBoardImage.setImgName(savedName);
                        communityBoardImage.setImgUrl("/uploads/" + savedName);
                        communityBoardImageRepository.save(communityBoardImage);
                    } catch (IOException e) {
                        throw new RuntimeException("이미지 저장 실패",e);
                    }
                }
            }
        }

        // 그래프 정보 저장
        if (communityWriteDto.getStartDate() != null && communityWriteDto.getFinishDate() != null) {
            CommunityBoardGraph graph = new CommunityBoardGraph();
            graph.setCommunityBoard(communityBoard);
            graph.setStartDate(communityWriteDto.getStartDate().atStartOfDay());
            graph.setFinishDate(communityWriteDto.getFinishDate().atTime(23, 59, 59));
            communityBoardGraphRepository.save(graph);
        }

        return communityBoard.getId();
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


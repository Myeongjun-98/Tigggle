package com.Tigggle.Service.community;

import com.Tigggle.Constant.Community.CommunityCategory;
import com.Tigggle.Constant.Community.SearchType;
import com.Tigggle.DTO.community.*;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.community.CommunityBoard;
import com.Tigggle.Entity.community.CommunityBoardGraph;
import com.Tigggle.Entity.community.CommunityBoardImage;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Repository.community.CommunityBoardGraphRepository;
import com.Tigggle.Repository.community.CommunityBoardImageRepository;
import com.Tigggle.Repository.community.CommunityBoardRepository;
import com.Tigggle.Repository.community.CommunityCommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public PageInfoDto getPageInfo(int currentPage, int totalPages, int blockSize) {

        PageInfoDto pageInfoDto = new PageInfoDto();

        // currentPage 현재 페이지 번호 (1부터 시작)
        // totalPages 전체 페이지 수
        // blockSize 한번에 보여줄 페이지 블록 양 (예: 10이면 1~10, 11~20)

        int blockNum = currentPage / blockSize;
        int startPage = blockNum * blockSize + 1;
        int endPage = Math.min(startPage + blockSize - 1, totalPages);

        pageInfoDto.setStartPage(startPage);
        pageInfoDto.setEndPage(endPage);
        pageInfoDto.setCurrentPage(currentPage + 1);
        pageInfoDto.setTotalPages(totalPages);

        pageInfoDto.setHasPrev(startPage > 1);
        pageInfoDto.setHasNext(endPage < totalPages);

        return pageInfoDto;
    }

    public Page<CommunityBoardListDto> getCommunityBoards(CommunityCategory communityCategory,
                                                          Pageable pageable) {

        Page<CommunityBoard> communityBoards = communityBoardRepository.
                findByCommunityCategoryAndDeletedIsFalseOrderByWriteDateDesc(
                        communityCategory, pageable);

        List<CommunityBoardListDto> communityBoardListDtos = new ArrayList<>();

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

        return new PageImpl<>(communityBoardListDtos, pageable, communityBoards.getTotalElements());

    }

//    팁 게시판 저장 메서드
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
        if (communityWriteDto.getGraphs() != null) {
            for (CommunityGraphDto communityGraphDto : communityWriteDto.getGraphs()) {
                CommunityBoardGraph communityBoardGraph = communityGraphDto.to(communityBoard);
                communityBoardGraphRepository.save(communityBoardGraph);
            }
        }

        return communityBoard.getId();
    }

//    예적금 분석 게시판 메서드
    public Long saveDiscussionBoard(CommunityWriteDto communityWriteDto,
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

        return communityBoard.getId();
    }

    public Long saveEconomicMarketBoard(CommunityWriteDto communityWriteDto,
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

        return communityBoard.getId();
    }

    // 조회수 증가
    @Transactional
    public void incrementHit(Long id) {
        CommunityBoard communityBoard = communityBoardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
        communityBoard.setHit(communityBoard.getHit() + 1);

    }

    // 상세페이지
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

    // 게시글 삭제
    @Transactional
    public CommunityCategory softDeletedPost(Long postId, String memberAccessId) {
        CommunityBoard communityBoard = communityBoardRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if(!communityBoard.getMember().getAccessId().equals(memberAccessId)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        communityBoard.setDeleted(true);

        return communityBoard.getCommunityCategory();
    }

    // 게시글 수정
    @Transactional
    public void updatePost(Long postId, CommunityWriteDto communityWriteDto,
                           String accessId) {

        CommunityBoard communityBoard = communityBoardRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        if(!communityBoard.getMember().getAccessId().equals(accessId)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        // 게시글 정보 수정
        communityBoard.setTitle(communityWriteDto.getTitle());
        communityBoard.setContent(communityWriteDto.getContent());
        communityBoard.setUpdateDate(LocalDateTime.now());
        communityBoardRepository.save(communityBoard);

        // 이미지 삭제 처리
        List<Long> deleteImageIds = communityWriteDto.getDeleteImageIds();
        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            for (Long imageId : deleteImageIds) {
                communityBoardImageRepository.deleteById(imageId);
            }
        }

        // ✅ 새 이미지 저장 로직 추가
        if (communityWriteDto.getImages() != null) {
            for (MultipartFile file : communityWriteDto.getImages()) {
                if (!file.isEmpty()) {
                    try {
                        String originalName = file.getOriginalFilename();
                        String uuid = UUID.randomUUID().toString();
                        String extension = originalName.substring(originalName.lastIndexOf("."));
                        String savedName = uuid + extension;
                        String fullPath = uploadDir + savedName;

                        file.transferTo(new File(fullPath));

                        CommunityBoardImage image = new CommunityBoardImage();
                        image.setCommunityBoard(communityBoard);
                        image.setOriginalName(originalName);
                        image.setImgName(savedName);
                        image.setImgUrl("/uploads/" + savedName);
                        communityBoardImageRepository.save(image);
                    } catch (IOException e) {
                        throw new RuntimeException("이미지 저장 실패", e);
                    }
                }
            }
        }

        // 기존 그래프 삭제
        communityBoardGraphRepository.deleteByCommunityBoardId(postId);

        // 새로운 그래프 저장
        if(communityWriteDto.getGraphs() != null) {
            for(CommunityGraphDto communityGraphDto : communityWriteDto.getGraphs()) {
                CommunityBoardGraph communityBoardGraph = communityGraphDto.to(communityBoard);
                communityBoardGraphRepository.save(communityBoardGraph);
            }
        }

        communityBoardRepository.save(communityBoard);
    }

    // 수정 폼에 기존 글 정보 전달용 DTO 생성 메서드
    public CommunityWriteDto getPostForEdit(Long postId, String accessId) {
        CommunityBoard board = communityBoardRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        if (!board.getMember().getAccessId().equals(accessId)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        CommunityDetailDto detailDto = getBoardDetail(postId);
        return CommunityWriteDto.from(detailDto);
    }

    public Page<CommunityBoardListDto> getTipBoards(Pageable pageable) {

        return getCommunityBoards(CommunityCategory.TIP, pageable);
    }

    public Page<CommunityBoardListDto> getDiscussionBoards(Pageable pageable) {

        return getCommunityBoards(CommunityCategory.DISCUSSION, pageable);
    }

    public Page<CommunityBoardListDto> getEconomicMarketBoards(Pageable pageable) {

        return getCommunityBoards(CommunityCategory.ECONOMIC_MARKET, pageable);
    }

    // Tip 게시판 검색 메서드
    public Page<CommunityBoardListDto> searchCommunityTip(
            CommunitySearchDto communitySearchDto, Pageable pageable) {

        Page<CommunityBoard> communityBoards;

        // 검색 타임과 키워드에 따른 분기 처리
        if (communitySearchDto.getKeyword() == null || communitySearchDto.getKeyword()
                .trim().isEmpty()) {
            // 키워드 없으면 해당 카테고리 게시글 전체 조회
            communityBoards = communityBoardRepository
                    .findByCommunityCategoryAndDeletedIsFalseOrderByWriteDateDesc(
                            communitySearchDto.getCategory(), pageable);
        } else {
            // 키워드 있을때
            if (communitySearchDto.getSearchType() == SearchType.WRITER) {
                communityBoards = communityBoardRepository
                        .findByCommunityCategoryAndMemberNameContainingAndDeletedIsFalseOrderByWriteDateDesc(
                                communitySearchDto.getCategory(), communitySearchDto.getKeyword(), pageable);
            } else {
                communityBoards = communityBoardRepository
                        .findByCommunityCategoryAndTitleContainingAndDeletedIsFalseOrderByWriteDateDesc(
                                communitySearchDto.getCategory(), communitySearchDto.getKeyword(), pageable);
            }
        }

        return communityBoards.map(board -> {
            int commentCount = communityCommentRepository.countByCommunityBoardIdAndDeletedFalse(board.getId());
            boolean hasImage = communityBoardImageRepository.existsByCommunityBoardId(board.getId());
            boolean hasGraph = communityBoardGraphRepository.existsByCommunityBoardId(board.getId());

            return CommunityBoardListDto.from(board, commentCount, hasImage, hasGraph);
        });
    }

    // Discussion 게시판 검색 메서드
    public Page<CommunityBoardListDto> searchCommunityDiscussion(
            CommunitySearchDto communitySearchDto, Pageable pageable) {

        Page<CommunityBoard> communityBoards;

        // 검색 타임과 키워드에 따른 분기 처리
        if(communitySearchDto.getKeyword() == null || communitySearchDto.getKeyword()
                .trim().isEmpty()) {
            // 키워드 없으면 해당 카테고리 게시글 전체 조회
            communityBoards = communityBoardRepository
                    .findByCommunityCategoryAndDeletedIsFalseOrderByWriteDateDesc(
                            communitySearchDto.getCategory(), pageable);
        } else {
            // 키워드 있을때
            if (communitySearchDto.getSearchType() == SearchType.WRITER) {
                communityBoards = communityBoardRepository
                        .findByCommunityCategoryAndMemberNameContainingAndDeletedIsFalseOrderByWriteDateDesc(
                                communitySearchDto.getCategory(),
                                communitySearchDto.getKeyword(), pageable);
            } else {
                communityBoards = communityBoardRepository
                        .findByCommunityCategoryAndTitleContainingAndDeletedIsFalseOrderByWriteDateDesc(
                                communitySearchDto.getCategory(),
                                communitySearchDto.getKeyword(), pageable);
            }
        }

        return communityBoards.map(communityBoard -> {

            int commentCount = communityCommentRepository
                    .countByCommunityBoardIdAndDeletedFalse(communityBoard.getId());
            boolean hasImage = communityBoardImageRepository
                    .existsByCommunityBoardId(communityBoard.getId());
            boolean hasGraph = communityBoardGraphRepository
                    .existsByCommunityBoardId(communityBoard.getId());

            return CommunityBoardListDto.from(communityBoard, commentCount, hasImage, hasGraph);
        });
    }

    // EconomicMarket 게시판 검색 메서드
    public Page<CommunityBoardListDto> searchCommunityEconomicMarket(
            CommunitySearchDto communitySearchDto, Pageable pageable) {

        Page<CommunityBoard> communityBoards;

        // 검색 타임과 키워드에 따른 분기 처리
        if (communitySearchDto.getKeyword() == null || communitySearchDto.getKeyword()
                .trim().isEmpty()) {
            // 키워드 없으면 해당 카테고리 게시글 전체 조회
            communityBoards = communityBoardRepository
                    .findByCommunityCategoryAndDeletedIsFalseOrderByWriteDateDesc(
                            communitySearchDto.getCategory(), pageable);
        } else {
            // 키워드 있을때
            if (communitySearchDto.getSearchType() == SearchType.WRITER) {
                communityBoards = communityBoardRepository
                        .findByCommunityCategoryAndMemberNameContainingAndDeletedIsFalseOrderByWriteDateDesc(
                                communitySearchDto.getCategory(),
                                communitySearchDto.getKeyword(), pageable);
            } else {
                communityBoards = communityBoardRepository
                        .findByCommunityCategoryAndTitleContainingAndDeletedIsFalseOrderByWriteDateDesc(
                                communitySearchDto.getCategory(),
                                communitySearchDto.getKeyword(), pageable);
            }
        }

        return communityBoards.map(communityBoard -> {

            int commentCount = communityCommentRepository
                    .countByCommunityBoardIdAndDeletedFalse(communityBoard.getId());
            boolean hasImage = communityBoardImageRepository
                    .existsByCommunityBoardId(communityBoard.getId());
            boolean hasGraph = communityBoardGraphRepository
                    .existsByCommunityBoardId(communityBoard.getId());

            return CommunityBoardListDto.from(communityBoard, commentCount, hasImage, hasGraph);
        });
    }
}


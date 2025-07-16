package com.Tigggle.Controller.community;

import com.Tigggle.Constant.Community.CommunityCategory;
import com.Tigggle.Constant.Community.SearchType;
import com.Tigggle.DTO.community.*;
import com.Tigggle.Service.community.CommunityBoardService;
import com.Tigggle.Service.community.CommunityCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor

public class CommunityController {

    private final CommunityBoardService communityBoardService;
    private final CommunityCommentService communityCommentService;

    @ModelAttribute("communitySearchDto")
    public CommunitySearchDto communitySearchDto() {
        return new CommunitySearchDto();
    }

    // tip 게시판
    @GetMapping("/communityTip")
    public String communityTip(@ModelAttribute("communitySearchDto")
                                   CommunitySearchDto communitySearchDto,
                               @PageableDefault(size = 15) Pageable pageable,
                               Model model) {

        communitySearchDto.setCategory(CommunityCategory.TIP);

        Page<CommunityBoardListDto> communityBoardListDtos = communityBoardService
                .searchCommunityTip(communitySearchDto, pageable);

        int currentPage = communityBoardListDtos.getNumber();
        int totalPages = communityBoardListDtos.getTotalPages();
        int blockSize = 10;

        if (communityBoardListDtos.isEmpty() && currentPage >= totalPages && totalPages > 0) {
            totalPages--; // 페이지 수에서 하나 빼주기
        }

        PageInfoDto pageInfoDto = communityBoardService
                .getPageInfo(currentPage, totalPages, blockSize);

        model.addAttribute("pageInfoDto", pageInfoDto);
        model.addAttribute("communityBoardListDto", communityBoardListDtos);
        model.addAttribute("searchTypes", SearchType.values());

        return "community/Tip";
    }

    // tip 게시판 작성 페이지
    @GetMapping("/community/TipWrite")
    public String tipWriteForm(Model model) {
        model.addAttribute("communityWriteDto", new CommunityWriteDto());  // 작성 폼용 DTO 초기화
        return "community/TipWrite";  // 작성 폼 뷰 이름
    }

    @PostMapping("/community/TipWrite")
    public String TipWritePost(@ModelAttribute CommunityWriteDto communityWriteDto,
                               Principal principal) {
        communityWriteDto.setCategory(CommunityCategory.TIP);

        Long savedId = communityBoardService.saveTipBoard(communityWriteDto, principal.getName());
        return "redirect:/communityTip";
    }

    // 상세페이지
    @GetMapping("/community/detail/{id}")
    public String Detail(@PathVariable Long id, Model model, Principal principal) {

        CommunityDetailDto communityDetailDto = communityBoardService.getBoardDetail(id);

        if(principal != null && !principal.getName()
                .equals(communityDetailDto.getMemberAccessId())) {
            communityBoardService.incrementHit(id);
        }

        model.addAttribute("DetailDto", communityDetailDto);

        if (principal != null) {
            model.addAttribute("loginUserName", principal.getName());
        }

        return switch (communityDetailDto.getCategory()) {
            case TIP -> "community/TipDetail";
            case DISCUSSION -> "community/DiscussionDetail";
            case ECONOMIC_MARKET -> "community/EconomicMarketDetail";
            default -> throw new IllegalStateException("알 수 없는 카테고리");
        };

    }

    // 수정 처리
    @GetMapping("/community/tip/edit/{id}")
    public String tipEditForm(@PathVariable Long id, Model model, Principal principal) {
        CommunityWriteDto dto = communityBoardService.getPostForEdit(id, principal.getName());
        model.addAttribute("communityWriteDto", dto);
        return "community/TipWrite"; // 작성 페이지 뷰 재활용
    }

    @PostMapping("/community/tip/edit/{id}")
    public String tipEditPost(@PathVariable Long id,
                              @ModelAttribute CommunityWriteDto dto,
                              Principal principal) {
        communityBoardService.updatePost(id, dto, principal.getName());
        return "redirect:/community/detail/" + id;
    }

    // 게시글 삭제
    @PostMapping("/community/delete/{id}")
    public String deletePost(@PathVariable Long id, Principal principal) {
        CommunityCategory communityCategory = communityBoardService
                .softDeletedPost(id, principal.getName());

        return switch (communityCategory) {
            case TIP -> "redirect:/communityTip";
            case DISCUSSION -> "redirect:/communityDiscussion";
            case ECONOMIC_MARKET -> "redirect:/communityEconomicMarket";
        };
    }

    // discussion 게시판
    @GetMapping("/communityDiscussion")

    public String communityDiscussion(@ModelAttribute("communitySearchDto")
                                      CommunitySearchDto communitySearchDto,
                                      @PageableDefault(size = 15) Pageable pageable,
                                      Model model) {

        communitySearchDto.setCategory(CommunityCategory.DISCUSSION);

        Page<CommunityBoardListDto> communityBoardListDtos = communityBoardService
                .searchCommunityDiscussion(communitySearchDto, pageable);

        int currentPage = communityBoardListDtos.getNumber();
        int totalPages = communityBoardListDtos.getTotalPages();
        int blockSize = 10;

        if (communityBoardListDtos.isEmpty() && currentPage >= totalPages && totalPages > 0) {
            totalPages--; // 페이지 수에서 하나 빼주기
        }

        PageInfoDto pageInfoDto = communityBoardService
                .getPageInfo(currentPage, totalPages, blockSize);

        model.addAttribute("pageInfoDto", pageInfoDto);
        model.addAttribute("communityBoardListDto", communityBoardListDtos);
        model.addAttribute("searchTypes", SearchType.values());

        return "community/Discussion";

    }

    // discussion 게시판 작성 페이지
    @GetMapping("/community/DiscussionWrite")
    public String discussionWriteForm(Model model) {
        model.addAttribute("communityWriteDto", new CommunityWriteDto());  // 작성 폼용 DTO 초기화
        return "community/DiscussionWrite";  // 작성 폼 뷰 이름
    }

    @PostMapping("/community/DiscussionWrite")
    public String discussionWritePost(@ModelAttribute CommunityWriteDto communityWriteDto,
                               Principal principal) {
        communityWriteDto.setCategory(CommunityCategory.DISCUSSION);

        Long savedId = communityBoardService.saveDiscussionBoard(communityWriteDto, principal.getName());
        return "redirect:/communityDiscussion";
    }

    // discussion 게시판 수정 처리
    @GetMapping("/community/discussion/edit/{id}")
    public String discussionEditForm(@PathVariable Long id, Model model, Principal principal) {
        CommunityWriteDto dto = communityBoardService.getPostForEdit(id, principal.getName());
        model.addAttribute("communityWriteDto", dto);
        return "community/DiscussionWrite"; // 작성 페이지 뷰 재활용
    }

    @PostMapping("/community/discussion/edit/{id}")
    public String discussionEditPost(@PathVariable Long id,
                              @ModelAttribute CommunityWriteDto dto,
                              Principal principal) {
        communityBoardService.updatePost(id, dto, principal.getName());
        return "redirect:/community/detail/" + id;
    }

    // EconomicMarket 게시판
    @GetMapping("/communityEconomicMarket")
    public String communityEconomicMarket(@ModelAttribute("communitySearchDto")
                                            CommunitySearchDto communitySearchDto,
                                          @PageableDefault(size = 15) Pageable pageable,
                                          Model model) {

        communitySearchDto.setCategory(CommunityCategory.ECONOMIC_MARKET);

        Page<CommunityBoardListDto> communityBoardListDtos = communityBoardService
                .searchCommunityEconomicMarket(communitySearchDto, pageable);

        int currentPage = communityBoardListDtos.getNumber();
        int totalPages = communityBoardListDtos.getTotalPages();
        int blockSize = 10;

        if (communityBoardListDtos.isEmpty() && currentPage >= totalPages && totalPages > 0) {
            totalPages--; // 페이지 수에서 하나 빼주기
        }

        PageInfoDto pageInfoDto = communityBoardService
                .getPageInfo(currentPage, totalPages, blockSize);

        model.addAttribute("pageInfoDto", pageInfoDto);
        model.addAttribute("communityBoardListDto", communityBoardListDtos);
        model.addAttribute("searchTypes", SearchType.values());

        return "community/EconomicMarket";
    }

    // EconomicMarket 게시판 작성 페이지
    @GetMapping("/community/EconomicMarketWrite")
    public String economicMarketWriteForm(Model model) {
        model.addAttribute("communityWriteDto", new CommunityWriteDto());  // 작성 폼용 DTO 초기화
        return "community/EconomicMarketWrite";  // 작성 폼 뷰 이름
    }

    @PostMapping("/community/EconomicMarketWrite")
    public String EconomicMarketWritePost(@ModelAttribute CommunityWriteDto communityWriteDto,
                                      Principal principal) {
        communityWriteDto.setCategory(CommunityCategory.ECONOMIC_MARKET);

        Long savedId = communityBoardService.saveEconomicMarketBoard(communityWriteDto, principal.getName());
        return "redirect:/communityEconomicMarket";
    }

    // EconomicMarket 게시판 수정
    @GetMapping("/community/economicMarket/edit/{id}")
    public String economicMarketEditForm(@PathVariable Long id, Model model, Principal principal) {
        CommunityWriteDto dto = communityBoardService.getPostForEdit(id, principal.getName());
        model.addAttribute("communityWriteDto", dto);
        return "community/EconomicMarketWrite"; // 작성 페이지 뷰 재활용
    }

    @PostMapping("/community/economicMarket/edit/{id}")
    public String economicMarketEditPost(@PathVariable Long id,
                                     @ModelAttribute CommunityWriteDto dto,
                                     Principal principal) {
        communityBoardService.updatePost(id, dto, principal.getName());
        return "redirect:/community/detail/" + id;
    }

    // 댓글 추가
    @PostMapping("/community/detail/{id}/comment")
    public String addComment(@PathVariable Long id,
                             @RequestParam("content")
                             String content,
                             Principal principal) {

        communityCommentService.addComment(id,  principal.getName(), content);

        return "redirect:/community/detail/" + id;
    }

    // 댓글 삭제
    @PostMapping("/community/comment/delete/{commentId}")
    public String deleteComment(@PathVariable Long commentId,
                                Principal principal) {

        communityCommentService.deleteComment(commentId, principal.getName());
        Long boardId = communityCommentService.getBoardIdByComment(commentId);

        return "redirect:/community/detail/" + boardId;
    }

    // 댓글 수정
    @PostMapping("/community/comment/update/{commentId}")
    public String updateComment(@PathVariable Long commentId,
                                @RequestParam String content,
                                Principal principal) {

        communityCommentService.updateComment(commentId, content, principal.getName());
        Long boardId = communityCommentService.getBoardIdByComment(commentId);

        return "redirect:/community/detail/" + boardId;
    }

}

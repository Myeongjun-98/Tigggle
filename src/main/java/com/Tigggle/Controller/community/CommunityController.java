package com.Tigggle.Controller.community;

import com.Tigggle.Constant.Community.CommunityCategory;
import com.Tigggle.DTO.community.CommunityBoardListDto;
import com.Tigggle.DTO.community.CommunityDetailDto;
import com.Tigggle.Service.community.CommunityBoardService;
import com.Tigggle.Service.community.CommunityCommentService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/communityTip")

    public String communityTip(Model model) {

        List<CommunityBoardListDto> communityBoardListDtos = communityBoardService
                .getCommunityBoards(CommunityCategory.TIP);
        model.addAttribute("communityBoardListDto", communityBoardListDtos);

        return "community/Tip";
    }

    @GetMapping("/community/detail/{id}")
    public String Detail(@PathVariable Long id, Model model, Principal principal) {

        CommunityDetailDto communityDetailDto = communityBoardService.getBoardDetail(id);
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

    @PostMapping("/community/detail/{id}/comment")
    public String addComment(@PathVariable Long id,
                             @RequestParam("content")
                             String content,
                             Principal principal) {

        communityCommentService.addComment(id,  principal.getName(), content);

        return "redirect:/community/detail/" + id;
    }

    @PostMapping("/community/comment/delete/{commentId}")
    public String deleteComment(@PathVariable Long commentId,
                                Principal principal) {

        communityCommentService.deleteComment(commentId, principal.getName());
        Long boardId = communityCommentService.getBoardIdByComment(commentId);

        return "redirect:/community/detail/" + boardId;
    }

    @PostMapping("/community/comment/update/{commentId}")
    public String updateComment(@PathVariable Long commentId,
                                @RequestParam String content,
                                Principal principal) {

        communityCommentService.updateComment(commentId, content, principal.getName());
        Long boardId = communityCommentService.getBoardIdByComment(commentId);

        return "redirect:/community/detail/" + boardId;
    }

    @GetMapping("/community/TipWrite")
    public String tipWriteForm() {

        return "community/TipWrite";
    }

    @GetMapping("/communityDiscussion")

    public String communityDiscussion(Model model) {

        List<CommunityBoardListDto> communityBoardListDtos = communityBoardService
                .getCommunityBoards(CommunityCategory.DISCUSSION);
        model.addAttribute("communityBoardListDto", communityBoardListDtos);

        return "community/Discussion";

    }

    @GetMapping("/community/DiscussionWrite")
    public String discussionWriteForm() {

        return "community/DiscussionWrite";
    }

    @GetMapping("/communityEconomicMarket")

    public String communityEconomicMarket(Model model) {

        List<CommunityBoardListDto> communityBoardListDtos = communityBoardService
                .getCommunityBoards(CommunityCategory.ECONOMIC_MARKET);
        model.addAttribute("communityBoardListDto", communityBoardListDtos);

        return "community/EconomicMarket";
    }

}

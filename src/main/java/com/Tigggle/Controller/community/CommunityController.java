package com.Tigggle.Controller.community;

import com.Tigggle.Constant.Community.CommunityCategory;
import com.Tigggle.DTO.community.CommunityBoardListDto;
import com.Tigggle.DTO.community.CommunityDetailDto;
import com.Tigggle.Service.community.CommunityBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor

public class CommunityController {

    private final CommunityBoardService communityBoardService;

    @GetMapping("/communityTip")

    public String communityTip(Model model) {

        List<CommunityBoardListDto> communityBoardListDtos = communityBoardService
                .getCommunityBoards(CommunityCategory.TIP);
        model.addAttribute("communityBoardListDto", communityBoardListDtos);

        return "community/Tip";
    }

    @GetMapping("/community/detail/{id}")
    public String Detail(@PathVariable Long id, Model model) {

        CommunityDetailDto communityDetailDto = communityBoardService.getBoardDetail(id);
        model.addAttribute("DetailDto", communityDetailDto);

        switch(communityDetailDto.getCategory()) {
            case TIP -> {

                return "community/TipDetail";
            }
            case DISCUSSION -> {

                return "community/DiscussionDetail";
            }
            case ECONOMIC_MARKET -> {

                return "community/EconomicMarketDetail";
            }
            default -> throw new IllegalStateException("알 수 없는 카테고리");
        }

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

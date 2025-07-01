package com.Tigggle.Controller.community;

import com.Tigggle.Constant.CommunityCategory;
import com.Tigggle.DTO.community.CommunityBoardListDto;
import com.Tigggle.Service.community.CommunityBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor

public class CommunityController {

    private final CommunityBoardService communityBoardService;

    @GetMapping("/communityTip")

    public String communityTip(Model model) {

        List<CommunityBoardListDto> communityBoardListDtos = communityBoardService
                .getCommunityBoards(CommunityCategory.TIP);
        model.addAttribute("communityBoardListDtos", communityBoardListDtos);

        return "community/Tip";

    }

    @GetMapping("/communityDiscussion")

    public String communityDiscussion(Model model) {

        List<CommunityBoardListDto> communityBoardListDtos = communityBoardService
                .getCommunityBoards(CommunityCategory.DISCUSSION);
        model.addAttribute("communityBoardListDtos", communityBoardListDtos);

        return "community/Discussion";

    }

    @GetMapping("/communityEconomicMarket")

    public String communityEconomicMarket(Model model) {

        List<CommunityBoardListDto> communityBoardListDtos = communityBoardService
                .getCommunityBoards(CommunityCategory.ECONOMIC_MARKET);
        model.addAttribute("communityBoardListDtos", communityBoardListDtos);

        return "community/EconomicMarket";
    }

}

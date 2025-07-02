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
    public String tipDetail(@PathVariable Long id, Model model) {

        CommunityDetailDto communityDetailDto = communityBoardService.getBoardDetail(id);
        model.addAttribute("TipDetailDto", communityDetailDto);
        return "community/TipDetail";
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

    @GetMapping("/communityEconomicMarket")

    public String communityEconomicMarket(Model model) {

        List<CommunityBoardListDto> communityBoardListDtos = communityBoardService
                .getCommunityBoards(CommunityCategory.ECONOMIC_MARKET);
        model.addAttribute("communityBoardListDto", communityBoardListDtos);

        return "community/EconomicMarket";
    }

}

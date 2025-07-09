package com.Tigggle.Controller.insite;

import com.Tigggle.DTO.asset.GoalConsumDto;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Service.insite.InsiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class assetController {

    private final InsiteService insiteService;
    private final UserRepository memberRepository;

    @GetMapping("/aaset")
    public String asset(){

        return "insite/asset";
    }


    @GetMapping("/api/asset/goal-summary")
    @ResponseBody
    public GoalConsumDto getGoalSummary(Principal principal) {
        Long memberId = memberRepository.findByAccessId(principal.getName()).getId();
        return insiteService.getKeywordSumByMember(memberId);
    }


}


package com.Tigggle.Controller.insite;

import com.Tigggle.DTO.asset.GoalConsumDto;
import com.Tigggle.DTO.insite.FixedAmountDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Service.insite.InsiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class assetController {

    private final InsiteService insiteService;
    private final UserRepository memberRepository;

    @GetMapping("/asset")
    public String showAssetPage(Model model, Principal principal){
        Long memberId = memberRepository.findByAccessId(principal.getName()).getId();
        GoalConsumDto goalConsumDto = new GoalConsumDto();

        model.addAttribute("memberId", memberId);
        model.addAttribute("GoalConsumDto", goalConsumDto);

        // 고정수입 대비 고정저축 상황 표현
        Member member = memberRepository.findByAccessId(principal.getName()); // 이거 필요한게 맞나??....
        FixedAmountDto fixedAmountDto = insiteService.getFixedAmountDto(member.getId());

        model.addAttribute("memberName", member.getName());
        model.addAttribute("income", fixedAmountDto.getIncome());
        model.addAttribute("saving", fixedAmountDto.getSaving());
        model.addAttribute("saveRate", fixedAmountDto.getSaveRate());

        return "insite/asset";

    }

//    @GetMapping("/api/asset/goal-summary")
//    @ResponseBody
//    public GoalConsumDto getGoalSummary(Model model, Principal principal) {
//        Long memberId = memberRepository.findByAccessId(principal.getName()).getId();
//        GoalConsumDto goalConsumDto = new GoalConsumDto();
//
//        model.addAttribute("memberId", memberId);
//        model.addAttribute("GoalConsumDto", goalConsumDto);
//
//        return insiteService.getKeywordSumByMember(memberId);
//    }


}


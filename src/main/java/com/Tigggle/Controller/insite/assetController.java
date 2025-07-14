package com.Tigggle.Controller.insite;

import com.Tigggle.DTO.asset.*;
import com.Tigggle.DTO.insite.AlertDto;
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

        GoalConsumDto goalConsumDto = insiteService.getKeywordSumByMember(memberId);

        model.addAttribute("memberId", memberId);
        model.addAttribute("goalConsumDto", goalConsumDto);

        // 키워드별 사용금액 가져오기
        AlertDto foodAlert = insiteService.getKeywordAlert(memberId, "식비", goalConsumDto.getFoodExpense());
        AlertDto commAlert = insiteService.getKeywordAlert(memberId, "주거/통신", goalConsumDto.getCommunication());
        AlertDto insuAlert = insiteService.getKeywordAlert(memberId, "보험", goalConsumDto.getInsurance());
        AlertDto etcAlert  = insiteService.getKeywordAlert(memberId, "기타", goalConsumDto.getEtc());

        model.addAttribute("foodAlert", foodAlert);
        model.addAttribute("commAlert", commAlert);
        model.addAttribute("insuAlert", insuAlert);
        model.addAttribute("etcAlert", etcAlert);


        // 고정수입 대비 고정저축 상황 표현
        Member member = memberRepository.findByAccessId(principal.getName()); // 이거 필요한게 맞나??....
        FixedAmountDto fixedAmountDto = insiteService.getFixedAmountDto(member.getId());

        model.addAttribute("memberName", member.getName());
        model.addAttribute("income", fixedAmountDto.getIncome());
        model.addAttribute("saving", fixedAmountDto.getSaving());
        model.addAttribute("saveRate", fixedAmountDto.getSaveRate());

        // 이번달 total 금액 호출
        MonthTotalDto monthTotalDto = insiteService.getMonthTotal(memberId);
        model.addAttribute("monthTotal", monthTotalDto);

        // 예/적금 , 입출금계좌 정보 호출
        SavingSummaryDto savingSummaryDto = insiteService.getSavingSummaryDto(member);
        model.addAttribute("memberName", member.getName());
        model.addAttribute("savingSummary", savingSummaryDto);

        // 총 목표 소비액 대비 사용금액 계산 로직
        Long gapAmount = insiteService.getGoalVsSpendingGapAmount(memberId);
        model.addAttribute("gapAmount", gapAmount);

        String gapMessage = insiteService.getGoalVsSpendingDifference(memberId);
        model.addAttribute("gapMessage", gapMessage);

        // 소비습관 로직
        ConsumptionHabitDto habit = insiteService.getConsumptionHabit(memberId);
        model.addAttribute("mostSpent", habit.getMostSpentCategory());
        model.addAttribute("leastSpent", habit.getLeastSpentCategory());
        model.addAttribute("mostAligned", habit.getMostAlignedCategory());



        // 절약 제안 로직
        SavingSuggestionDto suggestionDto = insiteService.getSavingSuggestion(memberId);
        model.addAttribute("suggestionDto", suggestionDto);

        return "insite/asset";

    }


}


package com.Tigggle.Controller.insite;


import com.Tigggle.DTO.insite.AgeGroupAverageDto;
import com.Tigggle.DTO.insite.InsiteReponseDto;
import com.Tigggle.DTO.insite.KeywordMonthlySpendingDto;
import com.Tigggle.DTO.insite.MonthlyDateDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Repository.insite.InsiteRepository;
import com.Tigggle.Service.insite.InsiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class InsiteController {

    private final InsiteService insiteService;
    private final UserRepository memberRepository;

    @GetMapping("/insite")
    public String showInsitePage(Model model, Principal principal) {
        Member member = memberRepository.findByAccessId(principal.getName()); //member정보 가져옴
        String ageGroup = insiteService.getAgeGroup(member.getBirthday()); //생일을 기준으로 문자열 리턴
        AgeGroupAverageDto ageGroupAverageDto = insiteService.getAgeGroupAverages(ageGroup);

        model.addAttribute("memberName", member.getName());
        model.addAttribute("ageGroupAverageDto", ageGroupAverageDto);
//        model.addAttribute("userSpending", userSpending != null ? userSpending : 0L);
//        model.addAttribute("groupAverage", groupAvg != null ? groupAvg : 0L);

        return "insite/insite";
    }


//    // 월간 소비내역 차트
//    private final InsiteService insiteService;
//
//    @GetMapping("/insite")
//    public String showMonthlyConsumptionChart(Model model){
//        LocalDate now = LocalDate.now();
//        Map<Integer, Long> monthlyData = insiteService.getMonthlyConsumption(now);
//
//        // key(월) value(소비액)을 모델에 넘김
//        model.addAttribute("months", monthlyData.keySet());
//        model.addAttribute("amount", monthlyData.values());
//
//        return "insite";
//    }

    @GetMapping("/api/insite/spending-summary")
    @ResponseBody
    public InsiteReponseDto getSpendingSummary(
            Principal principal, @RequestParam(required = false) String keyword){

        Long memberId = memberRepository.findByAccessId(principal.getName()).getId();

        return insiteService.getSixMonthSpendingSummary(memberId, keyword);
    }

    @GetMapping("/api/insite/keyword-monthly")
    @ResponseBody
    public List<KeywordMonthlySpendingDto> getKeywordMonthlyChart(Principal principal) {
        Long memberId = memberRepository.findByAccessId(principal.getName()).getId();
        return insiteService.getKeywordMonthlyChart(memberId);
    }

//    @GetMapping("/insite/age-average")
//    public String showAgeGroupAverages(Model model) {
//        List<AgeGroupAverageDto> avgList = insiteService.getAgeGroupAverages();
//        model.addAttribute("avgList", avgList);
//        return "insite/age-average"; // 해당 뷰 이름
//    }




}

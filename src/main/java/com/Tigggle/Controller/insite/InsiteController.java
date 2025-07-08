package com.Tigggle.Controller.insite;


import com.Tigggle.DTO.insite.InsiteReponseDto;
import com.Tigggle.DTO.insite.KeywordMonthlySpendingDto;
import com.Tigggle.Repository.UserRepository;
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

    private final UserRepository memberRepository;

    @GetMapping("/insite")
    public String insitePage(){
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

    private final InsiteService insiteService;
    @GetMapping("/api/insite/spending-summary")
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

}

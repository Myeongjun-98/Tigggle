package com.Tigggle.Controller.insite;


import com.Tigggle.DTO.insite.InsiteReponseDto;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Service.insite.InsiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class InsiteController {

    private final UserRepository memberRepository;

//    @GetMapping("/insite")
//    public String insite(){
//        return "/insite";
//    }


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
//
//
//    }

    private final InsiteService insiteService;

    @GetMapping("/api/insite/spending-summary")
    public InsiteReponseDto getSpendingSummary(
//            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Principal principal, @RequestParam(required = false) String keyword){

        Long memberId = memberRepository.findByAccessId(principal.getName()).getId();

        return insiteService.getSixMonthSpendingSummary(memberId, keyword);
    }

}

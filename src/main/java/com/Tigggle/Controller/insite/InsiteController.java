package com.Tigggle.Controller.insite;


import com.Tigggle.DTO.insite.AgeGroupAverageDto;
import com.Tigggle.DTO.insite.InsiteReponseDto;
import com.Tigggle.DTO.insite.KeywordMonthlySpendingDto;
import com.Tigggle.DTO.insite.MonthlyDateDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Repository.insite.InsiteRepository;
import com.Tigggle.Service.insite.InsiteExportService;
import com.Tigggle.Service.insite.InsiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
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
    private final InsiteExportService insiteExportService;

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


    // pdf 변환하기
    @GetMapping("/insite/export-pdf")
    public ResponseEntity<byte[]> exportInsiteToPdf(Principal principal) throws Exception {
        Long memberId = memberRepository.findByAccessId(principal.getName()).getId();
        Member member = memberRepository.findById(memberId).orElseThrow();

        ModelMap model = new ModelMap();

        // 기존 인사이트 데이터 불러오기
        InsiteReponseDto responseDto = insiteService.getSixMonthSpendingSummary(memberId, null);
        List<KeywordMonthlySpendingDto> keywordData = insiteService.getKeywordMonthlyChart(memberId);

        model.addAttribute("memberName", member.getName());
        model.addAttribute("responseDto", responseDto);
        model.addAttribute("keywordData", keywordData);

        byte[] pdfBytes = insiteExportService.generatePdf(model);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "insite-report.pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }



}

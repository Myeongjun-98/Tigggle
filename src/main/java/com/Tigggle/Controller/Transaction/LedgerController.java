package com.Tigggle.Controller.Transaction;

import com.Tigggle.DTO.Transaction.MonthlyLedgerDto;
import com.Tigggle.DTO.Transaction.WalletPageDto;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Service.Transaction.LedgerService;
import com.Tigggle.Service.Transaction.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class LedgerController {

    private final UserRepository memberRepository;
    private final WalletService walletService;

    @GetMapping("/wallet/page") // 새로운 통합 API 엔드포인트
    public ResponseEntity<WalletPageDto> getWalletPage(
            Principal principal,
            @RequestParam int year,
            @RequestParam int month) {

        Long memberId = memberRepository.findByAccessId(principal.getName()).getId();

        WalletPageDto pageData = walletService.getWalletPageData(memberId, year, month);

        return ResponseEntity.ok(pageData);
    }


}

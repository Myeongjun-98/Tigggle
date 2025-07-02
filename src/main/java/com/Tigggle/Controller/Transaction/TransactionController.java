package com.Tigggle.Controller.Transaction;


import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Tigggle.DTO.Transaction.CashDto;
import com.Tigggle.DTO.Transaction.OrdinaryAccountDto;
import com.Tigggle.Entity.Transaction.Asset;
import com.Tigggle.Entity.Transaction.Cash;
import com.Tigggle.Entity.Transaction.OrdinaryAccount;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Service.Transaction.TransactionService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;
    private final UserRepository memberRepository;

    // 멤버 아이디 찾기
    public Long getMemberId(Principal principal){
        return memberRepository.findByAccessId(principal.getName()).getId();
    };

    @GetMapping("/")
    public String DefaultTransactionPage(Principal principal, Model model){
        // 멤버 아이디
        Long memberId = getMemberId(principal);

        // 멤버아이디로 타입 불명의 디폴트 자산을 가져옴
        // 이 자산은 기본적으로 보통예금(Ordinary)를 반환, 없을 시 현금(Cash), 둘 다 아니라면 null 반환함
        Asset asset = transactionService.determineDefaultWalletAsset(memberId);

        if(!(asset == null)){
            // 가져온 자산이 보통예금이라면, 보통예금 DTO 반환
            if(asset instanceof OrdinaryAccount){
                OrdinaryAccountDto dto = transactionService.getDefaultOrdinary(asset);
                model.addAttribute("OrdinaryAccountDto", dto);
            }
            // 가져온 자산이 현금이라면, 현금DTO 반환
            if(asset instanceof Cash){
                CashDto dto = transactionService.getDefaultCash(asset);
                model.addAttribute("CashDto", dto);
            }
            else System.out.println("알 수 없는 종류의 자산입니다.");
        }

        return "/transaction/wallet";
    }



}

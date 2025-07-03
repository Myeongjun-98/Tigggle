package com.Tigggle.Controller.Transaction;


import java.security.Principal;

import com.Tigggle.Entity.Member;
import com.Tigggle.Service.Transaction.AssetService;
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
    private final UserRepository memberRepository;

    private final TransactionService transactionService;
    private final AssetService assetService;
    // 멤버 아이디 찾기
    // public Long getMemberId(Principal principal){
    //     return memberRepository.findByAccessId(principal.getName()).getId();+++
    // }

    @GetMapping("/wallet")
    public String DefaultTransactionPage(Principal principal, Model model){

        Member memberInfo = memberRepository.findByAccessId(principal.getName());

        // 사용자의 계좌를 검색, OrdinaryAccount를 우선적으로 가져오나 혹 OrdinaryAccount가 없을 시 Cash를,
        // 그래도 없다면 반환하지 않음.
        Asset a = transactionService.determineDefaultWalletAsset(memberInfo.getId());
        if(!(a == null)){
            if(a instanceof OrdinaryAccount){
                model.addAttribute("OrdinaryAccountDto", transactionService.getDefaultOrdinary(a));
            }
            if(a instanceof Cash){
                model.addAttribute("CashDto", transactionService.getDefaultCash(a));
            }
        }
        // 사용자 이름
        model.addAttribute("memberInfo", memberInfo);

        return "transaction/wallet";
    }



}

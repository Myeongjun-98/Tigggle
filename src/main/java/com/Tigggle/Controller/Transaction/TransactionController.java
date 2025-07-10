package com.Tigggle.Controller.Transaction;


import java.security.Principal;
import java.util.List;

import com.Tigggle.DTO.Transaction.WalletPageDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.Keywords;
import com.Tigggle.Repository.Transaction.KeywordsRepository;
import com.Tigggle.Service.Transaction.AssetService;
import com.Tigggle.Service.Transaction.WalletService;
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
    private final WalletService walletService;
    private final KeywordsRepository keywordsRepository;

    @GetMapping("/wallet")
    public String DefaultTransactionPage(Principal principal, Model model){

        List<Keywords> keywords = keywordsRepository.findAll();

        Member memberInfo = memberRepository.findByAccessId(principal.getName());
        model.addAttribute("memberInfo", memberInfo);
        model.addAttribute("keywords", keywords);

        return "transaction/wallet";
    }



}

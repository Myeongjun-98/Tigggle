package com.Tigggle.Controller.Transaction;


import java.security.Principal;
import java.util.List;

import com.Tigggle.DTO.Transaction.AssetListDto;
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

        //! 구현 안할 것 같음! 최신순 필터링
        List<Keywords> keywords = keywordsRepository.findAll();
        model.addAttribute("keywords", keywords);
        //! 구현 안할 것 같음! 최신순 필터링

        Member memberInfo = memberRepository.findByAccessId(principal.getName());
        model.addAttribute("memberInfo", memberInfo);

        List<AssetListDto> assetListDtos = walletService.loadWalletList(memberInfo);
        model.addAttribute("AssetList", assetListDtos);

        return "transaction/wallet";
    }

    @GetMapping("/wallet/{assetId}")
    public String SpecificTransactionPage(Principal principal, Model model){

        //! 구현 안할 것 같음! 최신순 필터링
        List<Keywords> keywords = keywordsRepository.findAll();
        model.addAttribute("keywords", keywords);
        //! 구현 안할 것 같음! 최신순 필터링

        Member memberInfo = memberRepository.findByAccessId(principal.getName());
        model.addAttribute("memberInfo", memberInfo);

        List<AssetListDto> assetListDtos = walletService.loadWalletList(memberInfo);
        model.addAttribute("AssetList", assetListDtos);

        return "transaction/wallet";
    }

    @GetMapping("/scheduled-transaction")
    public String openPopUpScheduledTransactionPage(Model model, Principal principal){

        Member memberInfo = memberRepository.findByAccessId(principal.getName());
        model.addAttribute("memberInfo", memberInfo);

        List<Keywords> keywords = keywordsRepository.findAll();
        model.addAttribute("keywords", keywords);

        return "transaction/scheduled-transaction";
    }

}

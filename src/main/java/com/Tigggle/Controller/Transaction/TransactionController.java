package com.Tigggle.Controller.Transaction;


import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Tigggle.DTO.Transaction.AssetListDto;
import com.Tigggle.DTO.Transaction.DefaultWalletResponseDto;
import com.Tigggle.Service.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    // 가계부의 디폴트 페이지(보통예금 or 현금 페이지 열림)
    @GetMapping("/")
    public ResponseEntity<DefaultWalletResponseDto> DefaultTransactionPage(Model model, Principal principal){
        
        Asset defaultAsset = principal

        return "/transaction/wallet";
    }
}

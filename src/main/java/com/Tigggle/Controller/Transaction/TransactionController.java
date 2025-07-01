package com.Tigggle.Controller.Transaction;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Tigggle.Service.TransactionService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/bankAccount")
    public String transactionPage(Model model,
                                    Principal principal){


        return "/transaction/bankAccount";
    }
}

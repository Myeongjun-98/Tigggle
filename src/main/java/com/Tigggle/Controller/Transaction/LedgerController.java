package com.Tigggle.Controller.Transaction;

import com.Tigggle.DTO.Transaction.MonthlyLedgerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assets")
public class LedgerController {

    @GetMapping("/{assetId}/ledger")
    public ResponseEntity<MonthlyLedgerDto> getMonthlyLedger(
        @PathVariable Long assetId,
        @RequestParam int year,
        @RequestParam int month
    ){
        MonthlyLedgerDto monthlyLedgerDto =
    }


}

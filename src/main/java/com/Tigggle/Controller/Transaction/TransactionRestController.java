package com.Tigggle.Controller.Transaction;

import com.Tigggle.DTO.Transaction.AssetListDto;
import com.Tigggle.DTO.Transaction.TransactionCreateRequestDto;
import com.Tigggle.DTO.Transaction.TransactionDetailDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Repository.Transaction.AssetRepository;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Service.Transaction.AssetService;
import com.Tigggle.Service.Transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionRestController {
    private final TransactionService transactionService;
    private final UserRepository memberRepository;
    private final AssetRepository assetRepository;
    private final AssetService assetService;

    // * 거래내역 생성
    @PostMapping("")
    public ResponseEntity<Void> createTransaction(
            @RequestBody TransactionCreateRequestDto createDto,
            Principal principal
            ){

        Member member = memberRepository.findByAccessId(principal.getName());
        transactionService.createTransaction(createDto, member);
        return ResponseEntity.ok().build();
    }

    // * 거래내역 저장 모달에서 paymethod에 따라 option값 표현
    @GetMapping("/by-paymethod")
    public ResponseEntity<List<AssetListDto>> getAssetsByPayMethod(
        @RequestParam String payMethod,
        Principal principal
    ){

        // 서비스에 payMethod와 사용자 정보를 넘겨 자산 목록을 조회
        List<AssetListDto> assets = assetService.getAssetsByPayMethod(payMethod,
                memberRepository.findByAccessId(principal.getName()));

        return ResponseEntity.ok(assets);
    }

    // * 거래내역 클릭 시 자세히보기
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDetailDto> getTransactionDetail(
            @PathVariable Long transactionId,
            Principal principal
    ){
        Member member = memberRepository.findByAccessId(principal.getName());
        TransactionDetailDto transactionDetailDto = transactionService.getTransactionDetail(transactionId, member);
        return ResponseEntity.ok(transactionDetailDto);
    }

}

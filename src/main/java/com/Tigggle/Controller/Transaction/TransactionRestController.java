package com.Tigggle.Controller.Transaction;

import com.Tigggle.DTO.Transaction.AssetListDto;
import com.Tigggle.DTO.Transaction.TransactionCreateRequestDto;
import com.Tigggle.DTO.Transaction.TransactionDetailDto;
import com.Tigggle.DTO.Transaction.TransactionUpdateDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Repository.Transaction.AssetRepository;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Service.Transaction.AssetService;
import com.Tigggle.Service.Transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000")
@Log4j2
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

    // * 거래내역 (입금) 모달에서 option값 표현
    @GetMapping("/when-income")
    public ResponseEntity<List<AssetListDto>> getAssetsWhenIncome(
            @RequestParam boolean isConsumption,
            Principal principal
    ){
        // 서비스에 지출이 false인지 여부와 사용자 정보를 넘겨 자산 목록을 조회
        List<AssetListDto> assets = assetService.getIncomeAssets(isConsumption,
                memberRepository.findByAccessId(principal.getName()));

        return ResponseEntity.ok(assets);
    }

    // * 거래내역 저장(지출) 모달에서 paymethod에 따라 option값 표현
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
        log.info(transactionDetailDto);
        return ResponseEntity.ok(transactionDetailDto);
    }

    // * 내역 삭제 매핑
    @DeleteMapping("") // URL에서 ID를 제거
    public ResponseEntity<Void> deleteTransactions(
            @RequestBody List<Long> transactionIds, // Request Body에서 ID 리스트를 받음
            Principal principal) {

        Member member = memberRepository.findByAccessId(principal.getName());

        // 수정된 서비스 메소드 호출
        transactionService.deleteTransaction(transactionIds, member);

        return ResponseEntity.ok().build();
    }

    // * 내역 수정 매핑
    @PatchMapping("/{transactionId}")
    public ResponseEntity<Void> updateTransaction(
            @PathVariable Long transactionId,
            @RequestBody TransactionUpdateDto transactionUpdateDto,
            Principal principal){

        Member member = memberRepository.findByAccessId(principal.getName());
        transactionService.updateTransaction(transactionId, transactionUpdateDto, member);
        return ResponseEntity.ok().build();
    }

    // * 정기 거래내역에서 자산 불러오기
    @GetMapping("/assets-for-schedule") // URL을 더 명확하게 변경
    public ResponseEntity<List<AssetListDto>> getAssetsForSchedule(Principal principal) {
        Member member = memberRepository.findByAccessId(principal.getName());

        List<AssetListDto> assets = assetService.getAssetsForScheduling(member);

        return ResponseEntity.ok(assets);
    }
}

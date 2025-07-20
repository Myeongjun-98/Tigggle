package com.Tigggle.Controller.Transaction;

import com.Tigggle.DTO.Transaction.AssetBankDto;
import com.Tigggle.DTO.Transaction.AssetCreateDto;
import com.Tigggle.Entity.product.Bank;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Repository.product.BankRepository;
import com.Tigggle.Service.Transaction.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AddAssetController {

    private final AssetService assetService;
    private final BankRepository bankRepository;
    private final UserRepository memberRepository;

    @PostMapping("/api/assets")
    public ResponseEntity<?> createAsset(@RequestBody AssetCreateDto createDto,
                                         Principal principal) {
        try {
            assetService.createAsset(createDto, memberRepository.findByAccessId(principal.getName()));
            return ResponseEntity.ok(Map.of("success", true, "message", "자산이 성공적으로 등록되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * 자산 추가 모달의 드롭다운에 사용할 은행 목록을 조회하는 API
     */
    @GetMapping("/api/banks")
    public ResponseEntity<List<AssetBankDto>> getAllBanks() {
        List<Bank> banks = bankRepository.findAll();
        // List<Bank>를 List<BankDto>로 변환합니다.
        List<AssetBankDto> bankDtos = banks.stream()
                .map(AssetBankDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bankDtos);
    }

}

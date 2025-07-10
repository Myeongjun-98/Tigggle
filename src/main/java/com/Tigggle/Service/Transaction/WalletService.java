package com.Tigggle.Service.Transaction;

import com.Tigggle.DTO.Transaction.AssetSummaryDto;
import com.Tigggle.DTO.Transaction.MonthlyLedgerDto;
import com.Tigggle.DTO.Transaction.WalletPageDto;
import com.Tigggle.Entity.Transaction.Asset;
import com.Tigggle.Entity.Transaction.BankAccount;
import com.Tigggle.Entity.Transaction.Cash;
import com.Tigggle.Entity.Transaction.OrdinaryAccount;
import com.Tigggle.Repository.Transaction.AssetRepository;
import com.Tigggle.Repository.Transaction.BankAccountRepository;
import com.Tigggle.Repository.Transaction.CashRepository;
import com.Tigggle.Repository.Transaction.OrdinaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final LedgerService ledgerService; // 가계부 생성 로직을 위임
    private final AssetRepository assetRepository;
    private final CashRepository cashRepository;
    private final BankAccountRepository bankAccountRepository;

    public WalletPageDto getWalletPageData(Long memberId, int year, int month) {

        // 1. 표시할 기본 자산 결정 (기존 로직 재사용)
        List<Asset> walletAssets = assetRepository.findCashAndOrdinaryAssetsByMemberId(memberId);

        if (walletAssets.isEmpty()) {
            // 자산이 없으면 "자산 없음" 상태의 DTO 반환
            return new WalletPageDto("NO_ASSET", null, null);
        }

        // 우선순위에 따라 기본 자산 선택
        Asset defaultAsset = walletAssets.stream()
                .filter(asset -> asset instanceof OrdinaryAccount)
                .findFirst()
                .orElse(walletAssets.get(0));

        // 2. 찾은 자산 ID로 월별 가계부 데이터 생성 (LedgerService 로직 호출)
        MonthlyLedgerDto monthlyLedger = ledgerService.getMonthlyLedger(defaultAsset.getId(), year, month);

        // 3. 현재 자산의 요약 정보 생성
        AssetSummaryDto assetSummary = createAssetSummary(defaultAsset);

        // 4. 모든 정보를 최종 WalletPageDto에 담아 반환
        return new WalletPageDto("SUCCESS", assetSummary, monthlyLedger);
    }

    // Asset 엔티티를 AssetSummaryDto로 변환하는 헬퍼 메소드
    private AssetSummaryDto createAssetSummary(Asset asset) {
        if (asset instanceof OrdinaryAccount) { // OrdinaryAccount 타입이라면
            BankAccount ba = bankAccountRepository.findById(asset.getId()).orElseThrow(() -> new IllegalStateException("보통예금 계좌 불러오기 실패"));
            return new AssetSummaryDto(asset.getId(), asset.getAlias(), ba.getBank().getName(), ba.getAccountNumber(), ba.getBalance());
        } else { // Cash 라면
            Cash cash = cashRepository.findById(asset.getId()).orElseThrow(()->new IllegalStateException("현금정보 불러오기 실패"));
            return new AssetSummaryDto(asset.getId(), asset.getAlias(), null, null, cash.getBalance());
        }
    }
}

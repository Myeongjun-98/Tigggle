package com.Tigggle.Service.Transaction;

import com.Tigggle.DTO.Transaction.AssetListDto;
import com.Tigggle.DTO.Transaction.AssetSummaryDto;
import com.Tigggle.DTO.Transaction.MonthlyLedgerDto;
import com.Tigggle.DTO.Transaction.WalletPageDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.Asset;
import com.Tigggle.Entity.Transaction.BankAccount;
import com.Tigggle.Entity.Transaction.Cash;
import com.Tigggle.Entity.Transaction.OrdinaryAccount;
import com.Tigggle.Repository.Transaction.AssetRepository;
import com.Tigggle.Repository.Transaction.BankAccountRepository;
import com.Tigggle.Repository.Transaction.CashRepository;
import com.Tigggle.Repository.Transaction.OrdinaryRepository;
import com.Tigggle.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final LedgerService ledgerService; // 가계부 생성 로직을 위임
    private final AssetRepository assetRepository;
    private final CashRepository cashRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository memberRepository;
    private final TransactionService transactionService;

    public WalletPageDto getWalletPageData(Long memberId, int year, int month, Long assetId) {

        Asset currentAsset;
        Member member = memberRepository.findById(Math.toIntExact(memberId)).orElseThrow();

        // 1. assetId가 URL을 통해 전달된 경우 (링크를 클릭했을 때)
        if (assetId != null) {
            currentAsset = (Asset) assetRepository.findByIdAndMember(assetId, member).orElseThrow(() -> new SecurityException("조회할 권한이 없는 자산입니다."));
        }
        // 2. assetId가 전달되지 않은 경우 (가계부 페이지로 처음 들어왔을 때)
        else {

        // * 1. 표시할 기본 자산 결정 (기존 로직 재사용)
        List<Asset> walletAssets = assetRepository.findCashAndOrdinaryAssetsByMemberId(memberId);

        if (walletAssets.isEmpty()) {
            // 자산이 없으면 "자산 없음" 상태의 DTO 반환
            return new WalletPageDto("NO_ASSET", null, null);
        }

        // 우선순위에 따라 기본 자산 선택
        currentAsset = walletAssets.stream()
                .filter(asset -> asset instanceof OrdinaryAccount)
                .findFirst()
                .orElse(walletAssets.get(0));
        }

        // 2. 찾은 자산 ID로 월별 가계부 데이터 생성 (LedgerService 로직 호출)
        MonthlyLedgerDto monthlyLedger = ledgerService.getMonthlyLedger(currentAsset.getId(), year, month);

        // 3. 현재 자산의 요약 정보 생성
        AssetSummaryDto assetSummary = createAssetSummary(currentAsset);

        // 4. 모든 정보를 최종 WalletPageDto에 담아 반환
        return new WalletPageDto("SUCCESS", assetSummary, monthlyLedger);
    }

    // * 페이지에 지갑 자산들의 링크 리스트 만들기
    public List<AssetListDto> loadWalletList(Member member){
        List<Asset> assetList = assetRepository.findCashAndOrdinaryAssetsByMemberId(member.getId());
        ArrayList<AssetListDto> assetListDtos = new ArrayList<>();

        for(Asset asset : assetList){
            Long id = asset.getId();
            String alias = asset.getAlias();
            String type;
            if(asset instanceof OrdinaryAccount)
                type = "ORDINARY";
            else if (asset instanceof Cash) {
                type = "CASH";
            } else continue;


            AssetListDto dto = new AssetListDto(id, alias, type);
            assetListDtos.add(dto);
        }
        return assetListDtos;
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

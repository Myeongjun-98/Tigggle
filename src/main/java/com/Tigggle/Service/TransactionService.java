package com.Tigggle.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.Tigggle.DTO.Transaction.AssetListDto;
import com.Tigggle.DTO.Transaction.CashDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.Asset;
import com.Tigggle.Entity.Transaction.Cash;
import com.Tigggle.Entity.Transaction.OrdinaryAccount;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Repository.Transaction.AssetRepository;
import com.Tigggle.Repository.Transaction.CashRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AssetRepository assetRepository;
    private final CashRepository cashRepository;
     private final UserRepository memberRepository;


    public Asset determineDefaultWalletAsset(Long memberId) {
        // 1. Repository를 통해 '지갑'에 해당하는 모든 자산(현금, 보통예금)을 조회합니다.
        List<Asset> walletAssets = assetRepository.findCashAndOrdinaryAssetsByMemberId(memberId);

        // 2. 비어있는지 확인 (둘 다 없는 경우)
        if (walletAssets.isEmpty()) {
            return null; // 자산이 없음을 알리기 위해 null 반환
        }

        // 3. 우선순위 적용: OrdinaryAccount가 있는지 먼저 확인합니다.
        // walletAssets 리스트에서 타입이 OrdinaryAccount인 것을 찾아보고, 있다면 첫 번째 것을 반환합니다.
        Optional<Asset> ordinaryAccount = walletAssets.stream()
                .filter(asset -> asset instanceof OrdinaryAccount)
                .findFirst();
        
        if (ordinaryAccount.isPresent()) {
            return ordinaryAccount.get(); // OrdinaryAccount가 있으므로, 이 자산을 기본값으로 결정!
        }
        
        // 4. OrdinaryAccount가 없다면, CashAsset이 유일한 옵션이므로 첫 번째 자산을 반환합니다.
        // 이 시점에 리스트에는 CashAsset만 남아있게 됩니다.
        return walletAssets.get(0);
    }

   // 현금 정보 DTO에 담기
   public CashDto cashInfo(Long cashId){
    Optional<Cash> cash = cashRepository.findById(cashId);

    CashDto cashDto = new CashDto();
    cashDto.setAlias(cash.get().getAlias());
    cashDto.setBalance(cash.get().getBalance());

    return cashDto;
   }

   


}

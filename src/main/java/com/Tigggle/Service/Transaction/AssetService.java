package com.Tigggle.Service.Transaction;

import com.Tigggle.DTO.Transaction.AssetListDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.*;
import com.Tigggle.Repository.Transaction.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AssetService {

    private final CashRepository cashRepository;
    private final OrdinaryRepository ordinaryRepository;
    private final CreditCardRepository creditCardRepository;
    private final AssetRepository assetRepository;
    private final BankAccountRepository bankAccountRepository;

    // * 잔액 계산 메서드
    @Transactional
    public void updateBalance(Asset asset, Long amount, boolean isConsumption) {

        if (asset == null) {
            return;
        }
        long changeAmount = isConsumption ? -amount : amount;

        // [핵심 해결책] 전달받은 Asset의 '타입'을 확인하는 대신,
        // 그 ID를 사용해 각 자식 Repository에서 '완전한 객체'를 직접 조회합니다.

        Cash cash = cashRepository.singleCash(asset.getId());
        if(cash != null){
            cash.setBalance(cash.getBalance() + changeAmount);
            cashRepository.save(cash);
            System.out.println("캐쉬쪽에서 업데이트한다/!!!!!!!!!");
        }
        else{
            OrdinaryAccount ordinaryAccount = ordinaryRepository.singleOrdinary(asset.getId());
            if (ordinaryAccount != null) {
                ordinaryAccount.setBalance(ordinaryAccount.getBalance() + changeAmount);
                ordinaryRepository.save(ordinaryAccount);
            System.out.println("Ordinary에서 업데이트한다/!!!!!!!!!");
            }
        }
    }

    // * 수입내역 작성 시 계좌리스트 불러오기
    public List<AssetListDto> getIncomeAssets(boolean isConsumption, Member member) {
        List<AssetListDto> assetListDtos;

        List<OrdinaryAccount> ordinaryAccounts = ordinaryRepository
                .findByMember(member);
        List<Cash> cashList = cashRepository.findByMember(member);

        List<Asset> combinedList = new ArrayList<>();
        combinedList.addAll(ordinaryAccounts);
        combinedList.addAll(cashList);

        assetListDtos = combinedList.stream().map(this::convertToAssetListDto)
                .toList();

        return assetListDtos;
    }

    // * 지출내역 작성 시 계좌리스트 불러오기
    public List<AssetListDto> getAssetsByPayMethod(String payMethod, Member member) {

        List<AssetListDto> assetListDtos;

        switch (payMethod) {
            case "NORMAL":
            case "MY_ACCOUNT_TRANSFER":
                List<OrdinaryAccount> ordinaryAccounts = ordinaryRepository
                        .findByMember(member);
                List<Cash> cashList = cashRepository.findByMember(member);

                List<Asset> combinedList = new ArrayList<>();
                combinedList.addAll(ordinaryAccounts);
                combinedList.addAll(cashList);

                assetListDtos = combinedList.stream().map(this::convertToAssetListDto)
                        .toList();
                break;
            case "CREDIT_CARD":
                List<CreditCard> creditCards = creditCardRepository.findByMember(member);
                assetListDtos = creditCards.stream().map(this::convertToAssetListDto).toList();
                break;

            default:throw new IllegalArgumentException("알 수 없는 결제 방식입니다.");
        }
        return assetListDtos;
    }

    private AssetListDto convertToAssetListDto(Asset asset) {

        String type;
        String name;

        if (asset instanceof CreditCard) {
            type = "신용카드";
            name = ((Card) asset).getCardName();
        } else if (asset instanceof OrdinaryAccount) {
            type = "보통예금";
            name = asset.getAlias();
        } else if (asset instanceof Cash) {
            type = "현금";
            name = asset.getAlias();
        } else {
            type = "기타 자산";
            name = asset.getAlias();
        }

        return new AssetListDto(asset.getId(), name, type);
    }

    // * 정기 거래 등록 시 사용할 자산 목록(모든 은행 계좌 + 현금)을 반환합니다.
    public List<AssetListDto> getAssetsForScheduling(Member member) {

        // 1. 모든 종류의 은행 계좌를 조회합니다.
        List<BankAccount> bankAccounts = bankAccountRepository.findByMember(member);
        // 2. 모든 현금 자산을 조회합니다.
        List<Cash> cashAssets = cashRepository.findByMember(member);
        // 3. 두 리스트를 모두 담을 수 있는 부모 타입(Asset)의 리스트를 새로 만듭니다.
        List<Asset> combinedList = new ArrayList<>();

        // 4. addAll()을 사용하여 두 리스트의 내용을 모두 합칩니다.
        combinedList.addAll(bankAccounts);
        combinedList.addAll(cashAssets);

        // 5. 합쳐진 리스트를 DTO 리스트로 변환하여 반환합니다.
        return combinedList.stream()
                .map(this::convertToAssetListDto)
                .collect(Collectors.toList());
    }

}

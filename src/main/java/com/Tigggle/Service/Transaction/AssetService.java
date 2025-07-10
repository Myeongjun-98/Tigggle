package com.Tigggle.Service.Transaction;

import com.Tigggle.DTO.Transaction.AssetListDto;
import com.Tigggle.DTO.Transaction.CashDto;
import com.Tigggle.DTO.Transaction.OrdinaryAccountDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.*;
import com.Tigggle.Repository.Transaction.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public void updateBalance(Long assetId, Long amount, boolean isConsumption) {

        // ! ▼▼▼▼▼ 로그 추가 ▼▼▼▼▼
        System.out.println("==================================================");
        System.out.println("[AssetService] updateBalance 호출됨!");
        System.out.println("  - Asset ID: " + assetId);
        System.out.println("  - Amount: " + amount);
        System.out.println("  - Is Consumption (지출 여부): " + isConsumption);
        // ! ▲▲▲▲▲ 로그 추가 ▲▲▲▲▲


        Asset asset = assetRepository.findById(assetId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자산입니다."));

        if (asset instanceof Cash) {
            Cash cash = (Cash) asset;
            long originalBalance = cash.getBalance();
            if (isConsumption) {
                cash.setBalance(originalBalance - amount);
            } else {
                cash.setBalance(originalBalance + amount);
            }
            //! ▼▼▼▼▼ 로그 추가 ▼▼▼▼▼
            System.out.println("  - [Cash] 잔액 변경: " + originalBalance + " -> " + cash.getBalance());
            System.out.println("==================================================");
            //! ▲▲▲▲▲  로그 추가 ▲▲▲▲▲
            cashRepository.saveAndFlush(cash);

        } else if (asset instanceof BankAccount) {
            BankAccount bankAccount = (BankAccount) asset;
            long originalBalance = bankAccount.getBalance();
            if (isConsumption) {
                bankAccount.setBalance(originalBalance - amount);
            } else {
                bankAccount.setBalance(originalBalance + amount);
            }
            //! ▼▼▼▼▼ 로그 추가 ▼▼▼▼▼
            System.out.println("  - [BankAccount] 잔액 변경: " + originalBalance + " -> " + bankAccount.getBalance());
            System.out.println("==================================================");
            //! ▲▲▲▲▲ 로그 추가 ▲▲▲▲▲
            bankAccountRepository.saveAndFlush(bankAccount);
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



}

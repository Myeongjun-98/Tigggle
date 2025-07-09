package com.Tigggle.Service.Transaction;

import com.Tigggle.DTO.Transaction.AssetListDto;
import com.Tigggle.DTO.Transaction.CashDto;
import com.Tigggle.DTO.Transaction.OrdinaryAccountDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.*;
import com.Tigggle.Repository.Transaction.AssetRepository;
import com.Tigggle.Repository.Transaction.CashRepository;
import com.Tigggle.Repository.Transaction.CreditCardRepository;
import com.Tigggle.Repository.Transaction.OrdinaryRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

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

    //* 현금 정보 DTO에 담기
    public CashDto cashInfo(Long cashId){
        Optional<Cash> cash = cashRepository.findById(cashId);

        CashDto cashDto = new CashDto();
        cashDto.setAlias(cash.get().getAlias());
        cashDto.setBalance(cash.get().getBalance());

        return cashDto;
    }

    //* 보통예금 정보 DTO에 담기
    public OrdinaryAccountDto ordinaryAccountInfo(Long OrdinaryAccountId){
        Optional<OrdinaryAccount> ordinary = ordinaryRepository.findById(OrdinaryAccountId);

        OrdinaryAccountDto accountDto = new OrdinaryAccountDto();
        accountDto.setAccountNumber(ordinary.get().getAccountNumber());
        accountDto.setAlias(ordinary.get().getAlias());
        accountDto.setBalance(ordinary.get().getBalance());
        accountDto.setBankLogo(ordinary.get().getBank().getLogoUrl());
        accountDto.setBankName(ordinary.get().getBank().getName());
        accountDto.setCompound(ordinary.get().isCompound());
        accountDto.setExpenseLimit(ordinary.get().getExpenseLimit());
        accountDto.setInterest(ordinary.get().getInterest());
        accountDto.setLimitType(ordinary.get().getLimitType());

        return accountDto;
    }

    @Transactional
    public void updateBalance(Long assetId, Long amount, boolean isConsumption) {
        Asset asset = assetRepository.findById(assetId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자산입니다."));

        if (asset instanceof Cash) {
            if (isConsumption) ((Cash) asset).setBalance(((Cash) asset).getBalance() - amount);
            else ((Cash) asset).setBalance(((Cash) asset).getBalance() + amount);
        }
        if(asset instanceof BankAccount){
            if (isConsumption) ((BankAccount) asset).setBalance(((BankAccount) asset).getBalance() - amount);
            else ((BankAccount) asset).setBalance(((BankAccount) asset).getBalance() + amount);
        }
    }

    public List<AssetListDto> getAssetsByPayMethod(String payMethod, Member member) {

        List<AssetListDto> assetListDtos = new ArrayList<>();

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

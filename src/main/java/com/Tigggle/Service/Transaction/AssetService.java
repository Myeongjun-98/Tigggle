package com.Tigggle.Service.Transaction;

import com.Tigggle.DTO.Transaction.AssetListDto;
import com.Tigggle.DTO.Transaction.CashDto;
import com.Tigggle.DTO.Transaction.OrdinaryAccountDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.Asset;
import com.Tigggle.Entity.Transaction.Cash;
import com.Tigggle.Entity.Transaction.CreditCard;
import com.Tigggle.Entity.Transaction.OrdinaryAccount;
import com.Tigggle.Repository.Transaction.CashRepository;
import com.Tigggle.Repository.Transaction.CreditCardRepository;
import com.Tigggle.Repository.Transaction.OrdinaryRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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

    // TODO 잔액 계산 로직 구상해야 함!!
    public void updateBalance(Long id, @NotNull Long amount, boolean b) {

    }

    public List<AssetListDto> getAssetsByPayMethod(String payMethod, Member member) {

        List<AssetListDto> assetListDtos = new ArrayList<>();

        switch (payMethod) {
            case "NORMAL":
            case "CREDIT_CARD":
                List<CreditCard> creditCards = creditCardRepository.findByMember(member);
                assetListDtos = creditCards.stream().map(this::convertToAssetListDto).toList();
                break;
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

            default:throw new IllegalArgumentException("알 수 없는 결제 방식입니다.");
        }
        return assetListDtos;
    }

    private AssetListDto convertToAssetListDto(Asset asset) {
        // Asset의 공통 필드를 사용하여 DTO를 생성합니다.
        return new AssetListDto(asset.getId(), asset.getAlias());
    }


}

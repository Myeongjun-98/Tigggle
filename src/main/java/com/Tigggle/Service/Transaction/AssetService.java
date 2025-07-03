package com.Tigggle.Service.Transaction;

import com.Tigggle.DTO.Transaction.CashDto;
import com.Tigggle.DTO.Transaction.OrdinaryAccountDto;
import com.Tigggle.Entity.Transaction.Cash;
import com.Tigggle.Entity.Transaction.OrdinaryAccount;
import com.Tigggle.Repository.Transaction.CashRepository;
import com.Tigggle.Repository.Transaction.OrdinaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AssetService {

    private final CashRepository cashRepository;
    private final OrdinaryRepository ordinaryRepository;

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
}

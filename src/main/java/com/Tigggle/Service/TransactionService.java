package com.Tigggle.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Tigggle.Entity.Transaction.Asset;
import com.Tigggle.Repository.Transaction.AssetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AssetRepository assetRepository;

    // // 사용자 자산 정보 가져오기
    // public List<Asset> myAssetList(Long userId){
    //     return assetRepository.findAllById(userId);
    // }

    // 사용자의 모든 계좌정보 가져오기
//    public List<Asset> findOrdinaryAndCash(Long userId){
//        List<String> OrdinaryAndCash = List.of("ORDINARY", "CASH");
//        return assetRepository.findByUserIdAndAssetTypeInOrderByIdAsc(userId, OrdinaryAndCash);
//    }

}

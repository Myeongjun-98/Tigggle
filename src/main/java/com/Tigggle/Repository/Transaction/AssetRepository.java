package com.Tigggle.Repository.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Tigggle.Entity.Transaction.Asset;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long>{

    // 사용자의 아이디로 모든 자산정보 가져오기
    public List<Asset> findAllById(Long userId);

    // 사용자의 아이디로 모든 계좌정보(개설일 기준) 가져오기
//    public List<Asset> findByUserIdAndAssetTypeInOrderByIdAsc(Long userId, List<String> assetTypes);


}

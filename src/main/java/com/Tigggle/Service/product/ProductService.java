package com.Tigggle.Service.product;

import com.Tigggle.Constant.FinancialProduct.DataSource;
import com.Tigggle.Constant.FinancialProduct.ProductType;
import com.Tigggle.Entity.product.Bank;
import com.Tigggle.Entity.product.Product;
import com.Tigggle.Repository.product.BankRepository;
import com.Tigggle.Repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BankRepository bankRepository;

    public void registerManualProduct(String bankName, String logoUrl, String homepageUrl,
                                      String productName, ProductType productType,
                                      int periodMonth, Long amountMoney, float interestRate) {

        // 1. 은행 존재 여부 확인
        Bank bank = bankRepository.findByName(bankName)
                .orElseGet(() -> {
                    // 없으면 새로 등록
                    Bank newBank = new Bank();
                    newBank.setName(bankName);
                    newBank.setLogoUrl(logoUrl);
                    newBank.setHomepageUrl(homepageUrl);
                    return bankRepository.save(newBank);
                });

        // 2. 상품 생성
        Product product = new Product();
        product.setBank(bank);
        product.setProductName(productName);
        product.setProductType(productType);
        product.setDataSource(DataSource.MANUAL);  // 수기 등록 표시
        product.setPeriodMonth(periodMonth);
        product.setAmountMoney(amountMoney);
        product.setInterestRate(interestRate);
        product.setIsActive(true);
        product.setCreatedDate(LocalDateTime.now());
        product.setUpdateDate(LocalDateTime.now());

        productRepository.save(product);
    }

    public void deleteProductById(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. id=" + productId));
        productRepository.delete(product);
    }

}

package com.Tigggle.DTO.product;

import com.Tigggle.Constant.DataSource;
import com.Tigggle.Constant.ProductType;
import com.Tigggle.Entity.product.Bank;
import com.Tigggle.Entity.product.Product;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class ProductListDto {

    private Long id; // 금융상품 목록 번호
    private Long bankId; // 은행 일련번호
    private String bankName; // 은행 이름
    private String logoUrl; // 은행 로고 주소
    private String homepageUrl; // 은행 사이트 주소
    private ProductType productType; // 상품 타입
    private DataSource dataSource; // 데이터 출처(API, MANUAL)
    private int periodMonth; // 상품 기간
    private Long amountMoney; // 가입 금액
    private Float interestRate; // 현재 금리
    private Long expectedAmount; // 예상수령 금액
    private Boolean isActive; // 활성화 여부
    private Boolean isRecommended; // 추천 여부
    private LocalDateTime createdDate; // 상품 등록일
    private LocalDateTime updateDate; // 상품 수정일

    public static ProductListDto from(Product product,
                                      User user,
                                      boolean isRecommended,
                                      Long expectedAmount) {

        ProductListDto productListDto = new ProductListDto();

        productListDto.setId(product.getId());
        productListDto.setBankId(product.getBank().getId());
        productListDto.setBankName(product.getBank().getName());
        productListDto.setLogoUrl(product.getBank().getLogoUrl());
        productListDto.setHomepageUrl(product.getBank().getHomepageUrl());
        productListDto.setProductType(product.getProductType());
        productListDto.setDataSource(product.getDataSource());
        productListDto.setPeriodMonth(product.getPeriodMonth());
        productListDto.setAmountMoney(product.getAmountMoney());
        productListDto.setInterestRate(product.getInterestRate());
        productListDto.setExpectedAmount(expectedAmount);
        productListDto.setIsActive(product.getIsActive());
        productListDto.setIsRecommended(isRecommended);
        productListDto.setCreatedDate(product.getCreatedDate());
        productListDto.setUpdateDate(product.getUpdateDate());

        return productListDto;
    }
}

package com.Tigggle.DTO.product;

import com.Tigggle.Constant.DataSource;
import com.Tigggle.Constant.ProductType;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.product.Product;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class ProductListDto {

    private Long id;
    private Long bankId;
    private String bankName;
    private String logoUrl;
    private String homepageUrl;
    private ProductType productType;
    private DataSource dataSource;
    private int periodMonth;
    private Long amountMoney;
    private Float interestRate;
    private Long expectedAmount;
    private Boolean isActive;
    private Boolean isRecommended;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

    public static ProductListDto from(Product product,
                                      Member member,
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

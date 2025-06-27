package com.Tigggle.DTO.product;

import com.Tigggle.Constant.DataSource;
import com.Tigggle.Constant.ProductType;
import lombok.Getter;
import lombok.Setter;

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
}

package com.Tigggle.DTO.product;

import com.Tigggle.Constant.DataSource;
import com.Tigggle.Constant.ProductType;
import com.Tigggle.Entity.User;
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
    private LocalDateTime cretedDate;
    private LocalDateTime updateDate;

//    public static ProductListDto from(Product product, User user, boolean isRecommended) {
//
//
//    }
}

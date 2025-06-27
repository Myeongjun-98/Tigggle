package com.Tigggle.DTO.product;

import com.Tigggle.Constant.ProductType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ProductSearchDto {

    private ProductType productType; // 상품 타입(유저 선택)
    private int periodMonth; // 상품 기간(유저 선택)
    private Long amountMoney; // 가입 금액(유저 선택)
    private String bankName; // 은행이름(유저 선택)
}

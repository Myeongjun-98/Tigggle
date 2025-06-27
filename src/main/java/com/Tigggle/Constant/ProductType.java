package com.Tigggle.Constant;

public enum ProductType {
    DEPOSIT("예금"), SAVINGS("적금");

    private final String label;

    ProductType(String label){this.label = label;}

    public String getLabel() {return this.label;}
}

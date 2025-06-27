package com.Tigggle.Constant;

public enum CommunityCategory {
    TIP("지출/저축tip"), DISCUSSION("예/적금 분석 및 토론"), ECONOMIC_MARKET("경제시장");

    private final String label;

    CommunityCategory(String label) {this.label = label;}

    public String getLabel() {return this.label;}
}

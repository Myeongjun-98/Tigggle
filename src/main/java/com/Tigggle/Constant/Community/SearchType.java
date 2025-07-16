package com.Tigggle.Constant.Community;

public enum SearchType {
    TITLE("제목"), WRITER("작성자");

    private final String label;

    SearchType(String label) {this.label = label;}

    public String getLabel() {return this.label;}
}

package com.jim.Partner_Match.entity.enums;

/**
 * 队伍状态枚举
 */
public enum TeamStatusEnum {
    PUBLIC(0, "公开"),
    SECRET(1, "加密"),
    PRIVATE(2, "私有");

    private int value;
    private String text;

    public static TeamStatusEnum getEnumByValue(int value) {
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for(TeamStatusEnum teamStatusEnum : values) {
            if(value == teamStatusEnum.getValue()) {
                return teamStatusEnum;
            }
        }
        return null;
    }

    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

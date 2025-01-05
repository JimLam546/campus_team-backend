package com.jim.Campus_Team.entity.enums;

import cn.hutool.core.util.StrUtil;

/**
 * @author Jim_Lam
 * @description UserRoleEnum
 */

public enum UserRoleEnum {

    USER("用户", 0),
    ADMIN("管理员", 1);

    private final String text;

    private final Integer value;

    UserRoleEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    public static UserRoleEnum getEnumByValue(Integer value) {
        if (value == null || value < 1) {
            return null;
        }

        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if (userRoleEnum.value.equals(value)) {
                return userRoleEnum;
            }
        }
        return null;
    }
}
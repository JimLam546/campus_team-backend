package com.jim.Partner_Match.common;

public enum ErrorCode {

    SUCCESS(0, "成功", ""),
    PARAMETER_ERROR(40000, "参数异常", ""),
    NULL_ERROR(40001, "请求参数为空", ""),
    NOT_LOGIN(40100, "没有登录", ""),
    NO_AUTO(40101, "没有权限", ""),
    USER_ERROR(400102, "账号异常", ""),
    USER_LOGIN_EXPIRE(400103, "session 过期", "身份过期"),
    SYSTEM_ERROR(50000, "服务器内部错误", "");


    private final int code;

    private final String message;
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}

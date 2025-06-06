package com.jim.Campus_Team.common;

public enum ErrorCode {

    SUCCESS(0, "成功", ""),
    PARAMETER_ERROR(40000, "参数异常", ""),
    NULL_ERROR(40001, "请求参数为空", ""),
    NOT_LOGIN(40100, "没有登录", ""),
    NO_AUTO(40101, "没有权限", ""),
    USER_ERROR(400102, "账号异常", ""),
    USER_LOGIN_EXPIRE(400103, "session 过期", "身份过期"),
    IS_FRIEND(4002001, "对方已是好友", "对方已是好友"),
    IS_OPERATE(4002002, "请求已被操作", "请求已被操作"),
    IS_REQUEST(4002003, "请勿重复发送申请", "请勿重复发送申请"),
    NOT_CHAT_HISTORY(4003001, "没有聊天记录", "没有聊天记录"),
    NOT_YOUR(4005001, "该内容不是你发布的", "不是你发布的"),
    NO_FRIEND(4004001, "不是您好友", "不是您好友"),
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

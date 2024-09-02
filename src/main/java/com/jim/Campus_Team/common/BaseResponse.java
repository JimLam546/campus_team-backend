package com.jim.Campus_Team.common;

import lombok.Data;

@Data
public class BaseResponse<T> {
    private int code;
    private String message;
    private T data;
    private String description;

    public BaseResponse(int code, String message, T data, String description) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.description = description;
    }


    public BaseResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseResponse(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage(), null, errorCode.getDescription());
    }

    public BaseResponse(ErrorCode errorCode, String description) {
        this(errorCode.getCode(), errorCode.getMessage(), null, description);
    }

    public BaseResponse(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

}

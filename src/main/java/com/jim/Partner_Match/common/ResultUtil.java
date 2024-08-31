package com.jim.Partner_Match.common;

public class ResultUtil{
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(0, data);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<T>(errorCode);
    }
    public static <T> BaseResponse<T> error(ErrorCode errorCode, String description) {
        return new BaseResponse<T>(errorCode, description);
    }


    public static <T> BaseResponse<T> error(int code, String message, String description) {
        return new BaseResponse<T>(code, message, description);
    }
}

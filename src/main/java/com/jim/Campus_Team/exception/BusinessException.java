package com.jim.Campus_Team.exception;

import com.jim.Campus_Team.common.ErrorCode;

public class BusinessException extends RuntimeException{
    private static final long serialVersionUID = 8457103549108688632L;
    private final int code;
    private final String description;
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public BusinessException(int code, String message, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }


    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

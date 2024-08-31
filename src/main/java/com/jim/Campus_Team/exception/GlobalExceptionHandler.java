package com.jim.Partner_Match.exception;

import com.jim.Partner_Match.common.BaseResponse;
import com.jim.Partner_Match.common.ErrorCode;
import com.jim.Partner_Match.common.ResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BusinessException.class})
    public BaseResponse businessException(BusinessException e) {
        return ResultUtil.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    @ExceptionHandler({RuntimeException.class})
    public BaseResponse runtimeExceptionHandler(RuntimeException e) {
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR);
    }
}

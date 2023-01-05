package com.example.grpcserviceclientmsgframe.exception;

import lombok.Getter;

/**
 * 业务逻辑异常
 *
 * @author ff
 **/
public class BizException extends RuntimeException {
    /**
     * 返回至前端的提示
     **/
    @Getter
    private int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }


}

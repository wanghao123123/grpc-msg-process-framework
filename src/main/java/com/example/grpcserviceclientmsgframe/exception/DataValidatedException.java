package com.example.grpcserviceclientmsgframe.exception;

/**
 * 业务逻辑异常:数据校验
 * @author ff
 * **/
public class DataValidatedException extends RuntimeException{
    public DataValidatedException(String errorMsg) {
        super(errorMsg);
    }
}

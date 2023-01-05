package com.example.grpcserviceclientmsgframe.exception;
/**
 * 业务逻辑异常：查询数据异常，为空
 * @author ff
 * **/
public class DataNullException extends RuntimeException{

    public DataNullException() {
    }

    public DataNullException(String message) {
        super(message);
    }
}

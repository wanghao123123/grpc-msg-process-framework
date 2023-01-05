package com.example.grpcserviceclientmsgframe.exception;
/**
 * 业务逻辑异常:查询的数据异常
 * @author ff
 * **/
public class DataException extends RuntimeException{
    public DataException() {
    }

    public DataException(String message) {
        super(message);
    }
}

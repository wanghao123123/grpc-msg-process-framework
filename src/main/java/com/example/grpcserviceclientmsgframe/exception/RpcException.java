package com.example.grpcserviceclientmsgframe.exception;

/**
 * 业务逻辑异常
 *
 * @author ff
 **/
public class RpcException extends RuntimeException {


    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(String message) {
        super(message);
    }
}

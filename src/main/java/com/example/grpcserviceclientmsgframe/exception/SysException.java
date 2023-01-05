package com.example.grpcserviceclientmsgframe.exception;

/**
 * 系统级异常
 *
 * @author ff
 **/
public class SysException extends RuntimeException {
    public SysException() {
    }

    public SysException(String message) {
        super(message);
    }

    public SysException(Throwable cause) {
        super(cause);
    }

    public SysException(String errorMsg, Throwable cause) {
        super(errorMsg, cause);
    }


}

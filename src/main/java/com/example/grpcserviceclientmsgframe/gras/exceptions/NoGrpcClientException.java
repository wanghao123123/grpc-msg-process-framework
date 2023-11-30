package hc.gras.exceptions;

import lombok.Getter;

/**
 * grpc 服务不存在异常处理
 **/
public class NoGrpcClientException extends RuntimeException {

    @Getter
    private final String grpcName;


    public NoGrpcClientException(String grpcName,Throwable cause){
        super(cause);
        this.grpcName=grpcName;

    }


}

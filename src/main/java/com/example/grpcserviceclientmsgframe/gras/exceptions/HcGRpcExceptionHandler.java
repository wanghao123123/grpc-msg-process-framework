package hc.gras.exceptions;

import com.alibaba.fastjson.JSON;
import com.hc.common.base.exception.HcException;
import io.grpc.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.recovery.GRpcExceptionHandler;
import org.lognet.springboot.grpc.recovery.GRpcExceptionScope;
import org.lognet.springboot.grpc.recovery.GRpcServiceAdvice;
import org.springframework.context.annotation.Configuration;

@Configuration
@GRpcServiceAdvice
@Slf4j
public class HcGRpcExceptionHandler {
    @GRpcExceptionHandler
    public Status handle (HcException exc, GRpcExceptionScope scope){
        ErrorInfo build = ErrorInfo.builder().code(exc.getCode()).msg(exc.getErrorEnum().getMessage() + "###" + exc.getErrorEnum().getChinese()).build();
        String errMsg = JSON.toJSONString(build);
        log.info("HcGRpcExceptionHandler ----  {}",errMsg);
        return Status.UNIMPLEMENTED.withDescription(errMsg);
    }
    @GRpcExceptionHandler
    public Status handle (IllegalArgumentException exc, GRpcExceptionScope scope){
        return Status.UNIMPLEMENTED.withDescription(exc.getMessage());
    }

    @GRpcExceptionHandler
    public Status handle (Exception exc, GRpcExceptionScope scope){
        return Status.UNIMPLEMENTED.withDescription(exc.getMessage());
    }


    @Getter
    @Setter
    @Builder(toBuilder = true)
    private static class ErrorInfo{

        private int code;
        private String msg;

    }
}

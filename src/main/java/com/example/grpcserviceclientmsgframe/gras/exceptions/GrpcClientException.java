package hc.gras.exceptions;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.Getter;

import javax.annotation.Nullable;

/**
 * 客户端调用异常处理
 **/
public class GrpcClientException extends StatusRuntimeException {
    /**
     * 项目名称
     **/
    @Getter
    private final String projectName;

    public GrpcClientException(String projectName, Status status) {
        super(status);
        this.projectName = projectName;
    }

    public GrpcClientException(String projectName,Status status, @Nullable Metadata trailers) {
        super(status, trailers);
        this.projectName = projectName;
    }
}

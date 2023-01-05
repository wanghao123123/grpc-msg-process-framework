package com.example.grpcserviceclientmsgframe.grpc;

import io.grpc.ClientInterceptor;

public class GrpcHeadersThreadLocal {
    private static final ThreadLocal<GrpcHeaders> GRPC_HEADERS_THREAD_LOCAL = new ThreadLocal<>();

    private GrpcHeadersThreadLocal() {
    }


    public static void set(GrpcHeaders grpcHeaders) {
        GRPC_HEADERS_THREAD_LOCAL.set(grpcHeaders);
    }

    public static ClientInterceptor getClientInterceptor() {
        return GrpcUtils.getClientInterceptors(GRPC_HEADERS_THREAD_LOCAL.get());
    }
    public static void remove(){
        GRPC_HEADERS_THREAD_LOCAL.remove();
    }
}

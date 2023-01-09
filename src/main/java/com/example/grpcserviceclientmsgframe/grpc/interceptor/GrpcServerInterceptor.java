package com.example.grpcserviceclientmsgframe.grpc.interceptor;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;

import java.util.Locale;
import java.util.Optional;

@GrpcGlobalServerInterceptor
@Order(1)
public class GrpcServerInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String lang = metadata.get(Metadata.Key.of("lang", Metadata.ASCII_STRING_MARSHALLER));
//        String currency = metadata.get(Metadata.Key.of("currency", Metadata.ASCII_STRING_MARSHALLER));
//        GrpcHeaders headers = new GrpcHeaders(lang, currency);
//        GrpcHeadersThreadLocal.add(headers);
        LocaleContextHolder.setLocale(new Locale(Optional.ofNullable(lang).orElse("en_us")));
        return serverCallHandler.startCall(serverCall, metadata);
    }
}

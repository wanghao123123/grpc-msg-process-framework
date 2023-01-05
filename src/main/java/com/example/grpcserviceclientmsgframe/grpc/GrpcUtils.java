package com.example.grpcserviceclientmsgframe.grpc;

import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;

public class GrpcUtils {

    /**
     * get grpc clientInterceptors
     *
     * @param headers headers
     **/
    public static ClientInterceptor getClientInterceptors(GrpcHeaders headers) {
        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of("lang", Metadata.ASCII_STRING_MARSHALLER), headers.getLang());
        metadata.put(Metadata.Key.of("currency", Metadata.ASCII_STRING_MARSHALLER), headers.getCurrency());
        return MetadataUtils.newAttachHeadersInterceptor(metadata);
    }


}

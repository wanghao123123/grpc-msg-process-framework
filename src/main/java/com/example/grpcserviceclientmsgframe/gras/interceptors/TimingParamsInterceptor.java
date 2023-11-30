package hc.gras.interceptors;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimingParamsInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {
        long startTime = System.currentTimeMillis();
        ClientCall<ReqT, RespT> call = next.newCall(method, callOptions);
        StringBuilder sb = new StringBuilder();
        sb.append("GRPC-Client   ");
        sb.append("ServerName:【"+method.getFullMethodName()+"】");
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(call) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {

                    @Override
                    public void onClose(Status status, Metadata trailers) {
                        // 响应关闭时记录时间
                        long endTime = System.currentTimeMillis();
                        long duration = (endTime - startTime); // 转换为毫秒
                        sb.append("Time:【" + duration + "/ms】");
                        log.info(sb.toString());
                        super.onClose(status, trailers);
                    }

                    @Override
                    public void onMessage(RespT message) {
                        sb.append("RespT:【" + message.toString() + "】");
                        super.onMessage(message);
                    }
                }, headers);
            }

            @Override
            public void sendMessage(ReqT message) {
                sb.append("ReqT:【" + message.toString() + "】");
                super.sendMessage(message);
            }
        };
    }
}

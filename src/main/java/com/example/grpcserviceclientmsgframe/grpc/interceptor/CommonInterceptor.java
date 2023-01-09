package com.example.grpcserviceclientmsgframe.grpc.interceptor;

import com.example.grpcserviceclientmsgframe.grpc.GrpcHeaders;
import com.example.grpcserviceclientmsgframe.grpc.GrpcHeadersThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
@Slf4j
public class CommonInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String lang = Optional.ofNullable(request.getHeader("lang")).orElse("");
        String currency = Optional.ofNullable(request.getHeader("currency")).orElse("");
        GrpcHeadersThreadLocal.set(new GrpcHeaders(lang, currency));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        GrpcHeadersThreadLocal.remove();
    }
}

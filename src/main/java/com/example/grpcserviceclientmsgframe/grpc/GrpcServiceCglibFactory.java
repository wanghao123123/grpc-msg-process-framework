package com.example.grpcserviceclientmsgframe.grpc;

import com.example.grpcserviceclientmsgframe.exception.NullSuccessException;
import com.example.grpcserviceclientmsgframe.exception.SysException;
import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 *
 * </p>
 *
 * @author hao.wong
 * @since 2023/1/4
 */
@Slf4j
public class GrpcServiceCglibFactory {


    public static <T> T getProxiedObject(Class clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new MethodExecute());
        T proxied = (T) enhancer.create();

        return proxied;
    }


    static class MethodExecute implements MethodInterceptor {

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            String[] $$s = o.getClass().getName().split("\\$");

            GrpcServiceClient grpcMethod = GrpcServiceRegister.getGrpcClass($$s[0]);
            Channel channel = GrpcServiceRegister.getChannel(grpcMethod.projectName().getName());

            //判断缓存是否存在
            Object invoke = GrpcServiceRegister.getServiceGrpcClassStub(grpcMethod.serviceGrpc().getName());

            if(Objects.isNull(invoke)){
                Optional<Method> newBlockingStub = Arrays.stream(grpcMethod.serviceGrpc().getDeclaredMethods()).filter(m -> m.getName().equals("newBlockingStub")).findFirst();
                if (newBlockingStub.isPresent()) {
                    try {
                        //get Stub By 'newBlockingStub' Method
                        invoke = newBlockingStub.get().invoke(grpcMethod.serviceGrpc(), channel);
                        //withInterceptors
                        ClientInterceptor interceptor = GrpcHeadersThreadLocal.getClientInterceptor();
                        for (Method declaredMethod : invoke.getClass().getDeclaredMethods()) {
                            if(declaredMethod.getName().equals("withInterceptors")){
                                declaredMethod.invoke(invoke,interceptor);
                            }
                        }
                        //register Stub By Annotation serviceGrpc field for Class Name
                        GrpcServiceRegister.addServiceGrpcClassStub(grpcMethod.serviceGrpc().getName(),invoke);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if(Objects.isNull(invoke)){
                throw new RuntimeException(grpcMethod.serviceGrpc().getName()+"---没有newBlockingStub方法，请指定正确的GrpcServiceClass");
            }
            //stub execution Rpc Method
            return execute(invoke, method.getName(), grpcMethod.projectName().getName(), objects);
        }

        /**
         * Grpc 执行远程调用
         *
         * @param obj         存根类
         * @param methodName  调用方法名
         * @param projectName 项目名
         * @param objects     请求参数
         * @return
         */
        public Object execute(Object obj, String methodName, String projectName, Object[] objects) {
            Optional<Method> method = Arrays.stream(obj.getClass().getDeclaredMethods()).filter(m -> m.getName().equals(methodName)).findFirst();

            if (method.isEmpty()) {
                throw new SysException("RPC服务请求异常,查询方法名不存在");
            }

            try {
                Object invoke = method.get().invoke(obj, objects);
                if (null == invoke || invoke.toString().isBlank()) {
                    throw new NullSuccessException();
                }
                return invoke;

            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (StatusRuntimeException e) {
//                String statusDesc = e.getStatus().getDescription();
//                if (StringUtils.isBlank(statusDesc)) {
//                    throw new RpcException(projectName + "  rpc error", e);
//                }
//                Integer rpcCode = JsonPathBuildUtils.read(statusDesc, "code_", null);
//                if (rpcCode == null) {
//                    throw new RpcException(projectName + "  rpc error", e);
//                }
//                log.info(">>> grpc return json:{}", statusDesc);
//                String msg = JsonPathBuildUtils.read(statusDesc, "msg_", "");
//                if (rpcCode >= RpcCodeConstants.BIZ_THRESHOLD) {
//                    //业务,校验类code 透传
//                    throw new BizException(rpcCode, msg);
//                } else if (rpcCode >= RpcCodeConstants.ERROR && RpcCodeConstants.ERROR < 600) {
//                    //错误code,做相应转换
//                    ResponseFailedEnums responseFailedEnums = ResponseFailedEnums.rpcCodeConvert(rpcCode);
//                    if (null == responseFailedEnums) {
//                        throw new SysException(projectName + ":unrecognized code:" + rpcCode + ",the msg:" + msg);
//                    }
//                    throw new ApiException(responseFailedEnums, projectName + ":ERROR", e);
//                }
//                throw new SysException(projectName + ":unrecognized code:" + rpcCode + ",the msg:" + msg);
            }
            return null;
        }

    }


}

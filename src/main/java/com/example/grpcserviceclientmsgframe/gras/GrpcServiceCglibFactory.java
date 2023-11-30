package hc.gras;

import com.hc.common.base.exception.ErrorEnum;
import com.hc.common.base.exception.HcException;
import hc.gras.annotations.GrpcServiceClient;
import hc.gras.exceptions.GrpcClientException;
import hc.gras.util.GrpcExceptionUtil;
import io.grpc.Channel;
import io.grpc.Status;
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
 */
@Slf4j
public class GrpcServiceCglibFactory {


    public static <T> T getProxiedObject(Class<?> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new MethodExecute());
        T proxied = (T) enhancer.create();

        return proxied;
    }


    static class MethodExecute implements MethodInterceptor {
        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws InvocationTargetException, IllegalAccessException {
            String[] $$s = o.getClass().getName().split("\\$");
            GrpcServiceClient grpcServiceClient = GrpcServiceRegister.getGrpcClass($$s[0]);
            String grpcClassName = grpcServiceClient.serviceGrpc().getName();
//            ClientInterceptor interceptor = GrpcHeadersThreadLocal.getClientInterceptor();
            Channel channel = GrpcServiceRegister.getChannel(grpcClassName);
            Method md = GrpcServiceRegister.getNewBlockingStubMethod(grpcServiceClient.serviceGrpc().getName());
            Object invoke;
            if (md != null) {
                invoke = md.invoke(grpcServiceClient.serviceGrpc(), channel);
                return execute(invoke, method.getName(), grpcClassName, objects);
            }

            try {
                md = grpcServiceClient.serviceGrpc().getDeclaredMethod("newBlockingStub", Channel.class);
            } catch (NoSuchMethodException e) {
//                throw new NoGrpcClientException(String.format("%s/%s ",projectName,grpcServiceClient.serviceGrpc().getSimpleName()),e);
            }
            invoke = md.invoke(grpcServiceClient.serviceGrpc(), channel);
            GrpcServiceRegister.addNewBlockingStubMethod(grpcServiceClient.serviceGrpc().getName(), md);

            return execute(invoke, method.getName(), grpcClassName, objects);
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
        public Object execute(Object obj, String methodName, String projectName, Object[] objects) throws GrpcClientException {
            Optional<Method> method = Arrays.stream(obj.getClass().getDeclaredMethods()).filter(m -> m.getName().equalsIgnoreCase(methodName)).findFirst();
            if (!method.isPresent()) {
                //方法名不存在
                throw new GrpcClientException(projectName + ":" + methodName, Status.NOT_FOUND.augmentDescription("method not found"));
            }
            Object invoke;
            try {
                invoke = method.get().invoke(obj, objects);
            } catch (StatusRuntimeException | InvocationTargetException | IllegalAccessException e) {
                if(e instanceof InvocationTargetException){
                    if(((InvocationTargetException) e).getTargetException() instanceof StatusRuntimeException) {
                        StatusRuntimeException targetException = (StatusRuntimeException) ((InvocationTargetException) e).getTargetException();
                        ErrorEnum error = GrpcExceptionUtil.getError(targetException.getStatus().getDescription());
                        if(Objects.nonNull(error)){
                            throw new HcException(error);
                        }
                        throw new GrpcClientException(projectName + ":" + methodName,targetException.getStatus());
                    }
                }
                throw new GrpcClientException(projectName + ":" + methodName, Status.fromThrowable(e));
            }
            return invoke;

        }

    }


}

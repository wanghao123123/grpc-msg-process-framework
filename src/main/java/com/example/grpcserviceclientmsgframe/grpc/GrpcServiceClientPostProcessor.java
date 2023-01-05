package com.example.grpcserviceclientmsgframe.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author hao.wong
 * @since 2023/1/4
 */
@Component
public class GrpcServiceClientPostProcessor implements BeanPostProcessor {

    @Autowired
    private Environment environment;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        GrpcServiceClient annotation = bean.getClass().getAnnotation(GrpcServiceClient.class);

        if (Objects.isNull(annotation)) {
            return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
        }

        //获取注解服务端地址配置
        String path = environment.getProperty(annotation.address());

        Channel channel = ManagedChannelBuilder.forTarget(path)
                .usePlaintext()
                .build();

        //registration Grpc Channel
        GrpcServiceRegister.addChannel(annotation.projectName().getName(), channel);
        //registration Class Registration Metadata
        GrpcServiceRegister.addGrpcClass(bean.getClass().getName(), annotation);

        return GrpcServiceCglibFactory.getProxiedObject(bean.getClass());
    }
}

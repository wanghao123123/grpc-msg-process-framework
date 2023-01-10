package com.example.grpcserviceclientmsgframe.grpc.v2;


import com.example.grpcserviceclientmsgframe.grpc.GrpcServiceClient;
import com.example.grpcserviceclientmsgframe.grpc.GrpcServiceRegister;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class GrpcClientBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private ResourcePatternResolver resourcePatternResolver;
    private CachingMetadataReaderFactory metadataReaderFactory;
    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private String[] scanBasePackages;


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> annotationMap = importingClassMetadata.getAnnotationAttributes(GrpcBeanScanner.class.getName(), true);
        if (CollectionUtils.isEmpty(annotationMap)) {
            return;
        }
        scanBasePackages = (String[]) annotationMap.get("scanBasePackages");
        if (scanBasePackages == null || scanBasePackages.length == 0) {
            return;
        }
        Set<BeanDomain> beanClazzSet = setScannerPackages();
        for (BeanDomain beanDomain : beanClazzSet) {
            Class<?> beanClazz = beanDomain.getClazz();
            String beanName = StringUtils.isBlank(beanDomain.getBeanName()) ? StringUtils.uncapitalize(beanClazz.getSimpleName()) : beanDomain.getBeanName();
            if (registry.isBeanNameInUse(beanName)) {
                //todo:同名问题处理
                continue;
            }
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanName);
            GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClazz);
            definition.setBeanClass(GrpcClientFactoryBean.class);
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            registry.registerBeanDefinition(beanName, definition);

        }

    }

    /**
     * 添加扫描包
     **/
    private Set<BeanDomain> setScannerPackages() {
        Set<BeanDomain> set = new LinkedHashSet<>();
        try {
            Resource[] resources = new Resource[]{};
            for (String scanPackage : scanBasePackages) {
                String folderPath = ClassUtils.convertClassNameToResourcePath(scanPackage);
                String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + folderPath + "/" + DEFAULT_RESOURCE_PATTERN;
                Resource[] resources1 = this.resourcePatternResolver.getResources(packageSearchPath);
                resources = ArrayUtils.addAll(resources1, resources);
            }
            for (Resource resource : resources) {
                if (!resource.isReadable()) {
                    continue;
                }
                MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                ClassMetadata classMetadata = metadataReader.getClassMetadata();
                // 只扫描接口
                if (!classMetadata.isInterface()) {
                    continue;
                }
                String className = classMetadata.getClassName();
                AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
                if (!annotationMetadata.hasAnnotation(GrpcServiceClient.class.getName())) {
                    continue;
                }
                Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(GrpcServiceClient.class.getName());
                assert attributes != null;
                Class<?> clazz = Class.forName(className);
                GrpcServiceClient grpcServiceClient = Class.forName(className).getAnnotation(GrpcServiceClient.class);
                BeanDomain beanDomain = BeanDomain.builder().clazz(clazz).beanName(Class.forName(className).getSimpleName()).build();
                set.add(beanDomain);
                //获取注解服务端地址配置 todo：获取地址方式有待调整
                String address = AppConfigUtils.get(grpcServiceClient.address());
                Channel channel = ManagedChannelBuilder.forTarget(address).usePlaintext().build();
                Channel mapChannel = GrpcServiceRegister.getChannel(grpcServiceClient.projectName().getName());
                if (Objects.isNull(mapChannel)) {
                    GrpcServiceRegister.addChannel(grpcServiceClient.projectName().getName(), channel);
                }
                GrpcServiceRegister.addGrpcClass(className, grpcServiceClient);
            }
        } catch (IOException | ClassNotFoundException e) {
            //todo:错误处理
            e.printStackTrace();

        }
        return set;
    }


    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }

    @Data
    @Builder
    private static class BeanDomain {
        private String beanName;
        private Class<?> clazz;
    }

}


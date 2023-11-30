package hc.gras;


import hc.gras.annotations.GrpcBeanScanner;
import hc.gras.annotations.GrpcServiceClient;
import hc.gras.interceptors.TimingParamsInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class GrpcClientBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
    private ResourcePatternResolver resourcePatternResolver;
    private CachingMetadataReaderFactory metadataReaderFactory;
    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private String[] scanBasePackages;
    private static Environment environment;


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
            String beanName = StringUtils.isEmpty(beanDomain.getBeanName()) ? StringUtils.uncapitalize(beanClazz.getSimpleName()) : beanDomain.getBeanName();
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
                resources = Stream.concat(Arrays.stream(resources), Arrays.stream(resources1))
                        .toArray(Resource[]::new);
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
                Class<?> clazz = Class.forName(className);
                GrpcServiceClient grpcServiceClient = clazz.getAnnotation(GrpcServiceClient.class);
                String grpcClassName = grpcServiceClient.serviceGrpc().getName();
                BeanDomain beanDomain = BeanDomain.builder().clazz(clazz).beanName(clazz.getSimpleName()).build();
                set.add(beanDomain);
                //获取注解服务端地址配置
                String address = resolve(grpcServiceClient.address());;
                Channel mapChannel = GrpcServiceRegister.getChannel(grpcClassName);
                if (Objects.isNull(mapChannel)) {
                    //todo:可能存在错误地址覆盖问题，合理处理多数据源问题
                    Channel channel = ManagedChannelBuilder.forTarget(address)
                            .intercept(new TimingParamsInterceptor())
                            .usePlaintext().build();
                    GrpcServiceRegister.addChannel(grpcClassName, channel);
                }
                GrpcServiceRegister.addGrpcClass(className, grpcServiceClient);

            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);

        }
        return set;
    }


    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }

    @Override
    public void setEnvironment(Environment environment) {
        GrpcClientBeanDefinitionRegistrar.environment=environment;
    }

    @Data
    @Builder
    private static class BeanDomain {
        private String beanName;
        private Class<?> clazz;
    }

    private String resolve(String value) {
        if (StringUtils.hasText(value)) {
            return this.environment.resolvePlaceholders(value);
        }
        return value;
    }

    private String getUrl(GrpcServiceClient grpcServiceClient) {
        String url = grpcServiceClient.address();
        return resolve(url);
    }
}


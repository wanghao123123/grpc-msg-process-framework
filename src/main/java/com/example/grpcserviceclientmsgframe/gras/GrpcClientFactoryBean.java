package hc.gras;

import org.springframework.beans.factory.FactoryBean;

public class GrpcClientFactoryBean<T> implements FactoryBean<T> {


    private final Class<T> interfaceType;

    public GrpcClientFactoryBean(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public T getObject() {
        return GrpcServiceCglibFactory.getProxiedObject(interfaceType);
    }

    @Override
    public Class<T> getObjectType() {
        return interfaceType;
    }


}
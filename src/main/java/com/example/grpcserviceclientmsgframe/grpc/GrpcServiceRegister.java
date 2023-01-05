package com.example.grpcserviceclientmsgframe.grpc;

import io.grpc.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * GrpcServiceClient元数据注册中心
 * </p>
 *
 * @author hao.wong
 * @since 2023/1/4
 */
public class GrpcServiceRegister {


    private final static Map<String, Channel> projectNameToChannel =new ConcurrentHashMap<>();
    private final static Map<String, GrpcServiceClient> classNameToClient =new ConcurrentHashMap<>();

    /**
     * key example:{@link spark.nft.proto.task.BlindBoxTaskServiceGrpc for Class Name}
     * value example:{@link spark.nft.proto.task.BlindBoxTaskServiceGrpc.BlindBoxTaskServiceBlockingStub for Object }
     */
    private final static Map<String, Object> serviceGrpcNameToStubObj =new ConcurrentHashMap<>();


    public static void addChannel(String projectName,Channel channel){
        projectNameToChannel.put(projectName, channel);
    }

    public static Channel getChannel(String projectName){
        return projectNameToChannel.get(projectName);
    }

    public static void addGrpcClass(String className,GrpcServiceClient client){
        classNameToClient.put(className, client);
    }

    public static GrpcServiceClient getGrpcClass(String className){
        return classNameToClient.get(className);
    }


    public static void addServiceGrpcClassStub(String className,Object stub){
        serviceGrpcNameToStubObj.put(className, stub);
    }

    public static Object getServiceGrpcClassStub(String className){
        return serviceGrpcNameToStubObj.get(className);
    }


}

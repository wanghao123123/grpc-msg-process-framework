package hc.gras;

import hc.gras.annotations.GrpcServiceClient;
import io.grpc.Channel;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GrpcServiceRegister {


    /**
     * className To Grpc Channel
     */
    private final static Map<String, Channel> classNameToChannel = new ConcurrentHashMap<>();
    private final static Map<String, GrpcServiceClient> classNameToClient = new ConcurrentHashMap<>();
    private final static Map<String, Method> methodMap = new ConcurrentHashMap<>();

    public static void addChannel(String projectName, Channel channel) {
        classNameToChannel.put(projectName, channel);
    }

    public static Channel getChannel(String projectName) {
        return classNameToChannel.get(projectName);
    }

    public static void addGrpcClass(String className, GrpcServiceClient client) {
        classNameToClient.put(className, client);
    }

    public static GrpcServiceClient getGrpcClass(String className) {
        return classNameToClient.get(className);
    }


    public static void addNewBlockingStubMethod(String className, Method method) {
        methodMap.put(className, method);
    }

    public static Method getNewBlockingStubMethod(String className) {
        return methodMap.get(className);
    }
}

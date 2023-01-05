package com.example.grpcserviceclientmsgframe.grpc;

import java.lang.annotation.*;

/**
 * <p>
 *
 * </p>
 *
 * @author hao.wong
 * @since 2023/1/4
 */
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface GrpcServiceClient {

    /**
     * 服务地址
     * @return
     */
    String address();

    /**
     * 项目名称
     * @return
     */
    ProjectNameEnums projectName() default ProjectNameEnums.QUERY_SERVICE;

    /**
     * Grpc 存根 Class
     * example：{@link  spark.nft.proto.task.BlindBoxTaskServiceGrpc.BlindBoxTaskServiceBlockingStub}
     * @return
     */
    Class stub();

    /**
     *  service Grpc Class
     *  example:{@link spark.nft.proto.task.BlindBoxTaskServiceGrpc}
     * @return
     */
    Class serviceGrpc();
}

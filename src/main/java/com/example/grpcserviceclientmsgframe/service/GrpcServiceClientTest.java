package com.example.grpcserviceclientmsgframe.service;

import com.example.grpcserviceclientmsgframe.grpc.GrpcServiceClient;
import com.example.grpcserviceclientmsgframe.grpc.ProjectNameEnums;
import org.springframework.stereotype.Service;
import spark.nft.proto.task.BlindBoxTaskServiceGrpc;
import spark.nft.proto.task.GetTaskReq;
import spark.nft.proto.task.TaskListResp;

/**
 * <p>
 *
 * </p>
 *
 * @author hao.wong
 * @since 2023/1/4
 */
@Service
@GrpcServiceClient(
        address = "grpc.server.blindboxtask.address",
        projectName = ProjectNameEnums.BLIND_BOX_TASK_SERVICE,
        serviceGrpc = BlindBoxTaskServiceGrpc.class,
        stub = BlindBoxTaskServiceGrpc.BlindBoxTaskServiceBlockingStub.class
)
public class GrpcServiceClientTest {


    public TaskListResp getTaskList(GetTaskReq req){return null;}
}

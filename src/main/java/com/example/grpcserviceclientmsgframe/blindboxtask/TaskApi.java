package com.example.grpcserviceclientmsgframe.blindboxtask;

import com.example.grpcserviceclientmsgframe.service.GrpcServiceClientTest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spark.nft.proto.task.GetTaskReq;
import spark.nft.proto.task.TaskListResp;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/nft/blindbox/task")
public class TaskApi {

    private final GrpcServiceClientTest test;

    @GetMapping(value = "/wh")
    public void getCollectionIntroduce(){
        TaskListResp taskList = test.getTaskList(GetTaskReq.newBuilder().setCollectionId(1).build());
        System.err.println();
    }
//
//    @ApiOperation(value = "系列作品列表")
//    @GetMapping(value = "/getNftWorksList")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "页", value = "pageNo", paramType = "query", dataTypeClass = Integer.class,required = true, example = "1"),
//            @ApiImplicitParam(name = "页大小", value = "pageSize", paramType = "query", dataTypeClass = Integer.class,required = true, example = "4"),
//            @ApiImplicitParam(name = "系列Id", value = "collectionId", paramType = "query", dataTypeClass = Long.class,required = true, example = "66"),
//            @ApiImplicitParam(name = "排序规则", value = "sort", paramType = "query", dataTypeClass = Integer.class,required = true, example = "1"),
//            @ApiImplicitParam(name = "模糊查询字段", value = "name", paramType = "query", dataTypeClass = String.class,required = true, example = "axy")
//    })
//    public NftWorksPageVo getNftWorksList(@RequestParam int pageNo,@RequestParam int pageSize,@RequestParam long collectionId,@RequestParam Integer sort ,@RequestParam String name){
//        NftWorksPageVo nftWorksList = collectionService.getNftWorksList(pageNo, pageSize, collectionId, sort, name);
//        return nftWorksList ;
//    }



}

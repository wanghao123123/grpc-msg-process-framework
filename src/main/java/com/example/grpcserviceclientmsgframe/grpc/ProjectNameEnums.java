package com.example.grpcserviceclientmsgframe.grpc;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ProjectNameEnums {
    /**
     * 账号资产服务
     **/
    ACCOUNT_SERVICE("account-service"),

    /**
     * NFT资产变动消息消费服务
     **/
    ASSET_CHANGE_CONSUMER_SERVICE("asset-change-consumer-service"),
    /**
     * nft交易服务
     **/
    TRADE_SERVICE("trade-service"),
    /**
     * 查询服务
     **/
    QUERY_SERVICE("query-service"),
    /**
     * 发布服务
     **/
    PUBLISH_SERVICE("publish-service"),


    /**
     * 智能合约服务
     **/
    CONTRACT_SERVICE("contract-service"),

    /**
     * 上传服务
     **/
    UPLOAD_SERVICE("upload-service"),
    /**
     * openEarth 活动服务
     **/
    OPEN_OPEN_EARTH_SERVICE("open-earth-service"),


    /**
     * 盲盒发售任务服务
     **/
    BLIND_BOX_TASK_SERVICE("blind-box-task-service"),


    /**
     * 统计服务
     **/
    STATISTIC_SERVICE("statistic-service");


    @Getter
    private String name;

}

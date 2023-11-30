package hc.gras.util;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import hc.gras.AckM;

import java.util.ArrayList;
import java.util.Arrays;

public class ProtoBufUtil {


    public static void main(String[] args){
//        AckMessageReq build = AckMessageReq.newBuilder()
//                .setUserId(0L)
//                .addAllSeqIds(new ArrayList<>(Arrays.asList(1L, 2L, 3L, 4L)))
//                .build();
//
//        //protoBuf 转为 JavaBean
//        AckM ackM = protoBufToBean(build, AckM.class);
//        //JavaBean 转为 protoBuf
//        AckMessageReq.Builder builder = AckMessageReq.newBuilder();
////        beanToProtoBuf(ackM,builder);
    }

    /**
     * 想要处理ProtoBuf 字段自带默认值的问题
     *  <p>
     *      需要在对应的proto文件 字段前面加上：
     *                oneof dto{
     *                  int64 userId=1;
     *               }
     *      再使用如下方法转换的时候：
     *          如果protobuf类没有显示赋值的时候转换后的Bean对象显示的NULL
     *  </p>
     * @param protoBuf
     * @param bean
     * @return
     * @param <T>
     * @param <R>
     */
    public static <T extends Message, R> R protoBufToBean(T protoBuf, Class<R> bean) {
        try {
            String print = JsonFormat.printer().includingDefaultValueFields().print(protoBuf);
            return JSON.parseObject(print, bean);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 想要处理ProtoBuf 字段自带默认值的问题
     *  <p>
     *      需要在对应的proto文件 字段前面加上：
     *                oneof dto{
     *                  int64 userId=1;
     *               }
     *      再使用如下方法将Bean转为ProtoBuf类的时候：
     *          需要自己显示判断该值是否被赋值。如果直接getUserId依旧会显示0默认值的情况
     *          需要使用builder.getDtoCase().getNumber() number指的是赋值的次数 如果>0就说明是显示赋值
     *  </p>
     * @param bean
     * @param protoBufClass
     * @param <T>
     * @param <R>
     */
    public static <T, R extends GeneratedMessageV3.Builder> void beanToProtoBuf(T bean, R protoBufClass) {
        try {
            JsonFormat.parser().ignoringUnknownFields().merge(JSON.toJSONString(bean), protoBufClass);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

}






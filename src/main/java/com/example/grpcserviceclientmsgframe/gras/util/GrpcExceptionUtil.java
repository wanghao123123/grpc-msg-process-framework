package hc.gras.util;

import com.alibaba.fastjson.JSONPath;
import com.hc.common.base.exception.ErrorEnum;

public class GrpcExceptionUtil {

    public static ErrorEnum getError(String status){
        try {
            return ErrorEnum.getErrorEnum((Integer) JSONPath.eval(status, "$.code"));
        }catch (NoClassDefFoundError | ClassCastException error){
            System.err.println(error.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        String s="{\n" +
                "    \"code\":1000001111,\n" +
                "    \"msg\":\"Request for server failed. Please try again###服务器忙，请稍后再试\"\n" +
                "}";
        ErrorEnum error = GrpcExceptionUtil.getError(s);
        System.err.println();
    }

}

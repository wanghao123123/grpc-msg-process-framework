package com.example.grpcserviceclientmsgframe.grpc.v2;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * 获取配置文件信息
 * todo:改获取方式有待调整
 * **/
public class AppConfigUtils {

    private static final ResourceLoader resourceLoader = new DefaultResourceLoader();


    public static String get(String key) {

        Resource resource = resourceLoader.getResource("application.properties");
        InputStream in = null;
        Properties p = null;
        try {
            in = resource.getInputStream();
            p = new Properties();
            p.load(in);
        } catch (IOException e) {
            //todo:异常处理
            return null;
        }
        return p.getProperty(key);
    }


}

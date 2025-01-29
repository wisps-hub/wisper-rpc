package com.wisper.common.config;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 使用配置文件
 */
public class ProtocolConfig {
    //名称
    public static String name;
    //主机port
    public static Integer port = 9999;
    //主机ip
    public static String host;
    //版本号
    public static String version = "1.0.0";
    //注册中心
    public static String registryAddress;
    public static String registryType;
    //序列化器
    public static String serializer = "HESSIAN";
    //负载均衡
    public static String loadBalance = "CONSISTENCY_HASH";

    public static Properties properties;

    static {
        try (InputStream in = ProtocolConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties = new Properties();
            properties.load(in);
            if (in == null) {
                throw new RuntimeException("未找到配置文件");
            }
            //注册中心
            String type = properties.getProperty("wisper.registry.type");
            if (StringUtils.isBlank(type)) {
                throw new RuntimeException("请选择注册中心");
            }
            registryType = type;
            String address = properties.getProperty("wisper.registry.address");
            if (StringUtils.isBlank(address)) {
                throw new RuntimeException("请配置注册中心");
            }
            registryAddress = address;
            //主机ip
            String protocolHost = properties.getProperty("wisper.protocol.host");
            //todo 主机ip可动态获取 这里测试写死127.0.0.1
            host = StringUtils.isNotBlank(protocolHost) ? protocolHost : "127.0.0.1";
            //端口
            String protocolPort = properties.getProperty("wisper.protocol.port");
            if (StringUtils.isNotBlank(protocolPort)) {
                port = Integer.parseInt(protocolPort);
            }
            //协议版本
            String protocolVersion = properties.getProperty("wisper.protocol.version");
            if (StringUtils.isNotBlank(protocolVersion)) {
                version = protocolVersion;
            }
            //序列化
            String protocolSerializer = properties.getProperty("wisper.protocol.serializer");
            if (StringUtils.isNotBlank(protocolSerializer)) {
                serializer = protocolSerializer;
            }
            //负载均衡
            String protocolLoadBalance = properties.getProperty("wisper.protocol.loadBalance");
            if (StringUtils.isNotBlank(protocolLoadBalance)) {
                loadBalance = protocolLoadBalance;
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

}
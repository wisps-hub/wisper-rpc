package com.wisper.common.utils;

import com.google.common.collect.Sets;
import com.wisper.common.config.ProtocolConfig;
import com.wisper.common.protocol.serialize.Serializer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Set;

public class ZKUtils {

    public static final CuratorFramework client;

    //zookeeper根路径节点
    public static final String ROOT_PATH = "MyRPC";

    //序列化算法
    private static final String SERIALIZE_ALG = "/serializeAlg";
    //重试白名单
    private static final String RETRY_WHITE_LIST = "retryWhiteList";


    //负责zookeeper客户端的初始化，并与zookeeper服务端进行连接
    static {
        client = CuratorFrameworkFactory.builder()
                .connectString(ProtocolConfig.registryAddress) //ip:port
                .sessionTimeoutMs(40000)//超时
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))//重试策略
                .namespace(ROOT_PATH)//根节点
                .build();
        client.start();
        System.out.println("zookeeper 连接成功");
    }

    public static void openWatch(String path, CuratorCacheListener listener){
        if (listener == null){
            return;
        }
        CuratorCache curatorCache = CuratorCache.build(client, "/" + path);
        // 设置监听器
        curatorCache.listenable().addListener(listener);
        // 开启监听
        curatorCache.start();
    }

    /**
     * 检查节点是否存在
     *
     * @param node 节点名称 方法内会自动用"/"拼接路径
     * @return boolean true 存在   false 不存在
     */
    public static boolean checkExists(String node){
        try {
            return ZKUtils.client.checkExists().forPath("/" + node) != null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建节点
     * 连带父路径一起创建
     *
     * @param createMode 持久/临时
     * @param node 节点名称 方法内会自动用"/"拼接路径
     */
    public static void createParentsIfNeeded(CreateMode createMode, String node){
        if (StringUtils.isBlank(node) || ZKUtils.checkExists(node)){
            return;
        }
        try {
            ZKUtils.client.create().creatingParentsIfNeeded()
                        .withMode(createMode).forPath("/" + node);
        } catch (Exception e) {
            throw new RuntimeException(String.format("创建zk节点异常, " +
                    "入参信息: [createMode: %s, node: %s], 异常信息: [%s]",
                    createMode, node, e.getCause().getMessage()));
        }
    }

    /**
     * 获取节点的子级列表
     *
     * @param parentNode 节点名称 方法内会自动用"/"拼接路径
     * @return List<String>
     */
    public static List<String> getChildList(String parentNode){
        if (StringUtils.isBlank(parentNode)){
            return Lists.newArrayList();
        }
        try {
            return ZKUtils.client.getChildren().forPath("/" + parentNode);
        } catch (Exception e) {
            throw new RuntimeException(String.format("获取子列表失败, " +
                    "入参信息: [node: %s], 异常信息: [%s] ", parentNode, e.getCause().getMessage()));
        }
    }

    /**
     * 获取序列化算法
     *
     * @return {@link Serializer.Algorithm}
     */
    public static Serializer.Algorithm getSerializeAlg(){
        try {
            byte[] bytes = ZKUtils.client.getData().forPath(SERIALIZE_ALG);
            String serializeAlg = new String(bytes);
            System.out.println("获取序列化算法成功: " + serializeAlg);
            return Serializer.Algorithm.getAlgorithm(serializeAlg);
        }catch (Exception e){
            System.out.println("获取序列化算法失败: " + e.getCause().getMessage());
            return Serializer.Algorithm.HESSIAN;
        }
    }

    /**
     *  获取重试白名单
     *
     * @return Set<String>
     */
    public static Set<String> getRetryWhiteList(){
        try {
            List<String> serviceList = ZKUtils.client.getChildren().forPath("/" + RETRY_WHITE_LIST);
            if (CollectionUtils.isEmpty(serviceList)) {
                return Sets.newHashSet();
            }
            return Sets.newHashSet(serviceList);
        } catch (Exception e) {
            System.out.println("获重试白名单失败: " + e.getCause().getMessage());
            return Sets.newHashSet();
        }
    }

    public static void addRetryWhiteList(String node){
        ZKUtils.createParentsIfNeeded(CreateMode.EPHEMERAL, RETRY_WHITE_LIST +"/"+ node);
    }

}

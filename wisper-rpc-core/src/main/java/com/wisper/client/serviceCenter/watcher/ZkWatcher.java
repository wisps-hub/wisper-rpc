package com.wisper.client.serviceCenter.watcher;

import com.wisper.common.utils.ZKUtils;
import com.wisper.client.serviceCenter.cache.ServiceCache;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

public class ZkWatcher {

    private ServiceCache serviceCache;

    public ZkWatcher(ServiceCache serviceCache) {
        this.serviceCache = serviceCache;
    }

    public void watchToUpdate(String path) {
        ZKUtils.openWatch("", new CuratorCacheListener() {
            /**
             * @param type 事件类型
             * @param beforeData 节点更新前的状态、数据
             * @param afterData 节点更新后的状态、数据
             * 创建节点时：beforeData = null
             * 删除节点时：afterData = null
             * 节点创建时没有赋予值 create /root/a 只创建节点 beforeData = null
             */
            @Override
            public void event(Type type, ChildData beforeData, ChildData afterData) {
                System.out.printf("zkWatcher数据 >>>type: [%s], before: [%s], after: [%s]%n", type, beforeData, afterData);
                switch (type) {
                    case NODE_CREATED:
                        // 监听器第一次执行时节点存在也会触发次事件
                        String[] pathList= parsePath(afterData);
                        if(pathList.length<=2) break;
                        else {
                            String serviceName=pathList[1];
                            String address=pathList[2];
                            //将新注册的服务加入到本地缓存中
                            serviceCache.add(serviceName,address);
                        }
                        break;
                    case NODE_CHANGED: // 节点更新 zk不支持直接修改节点名称 所以修改事件是针对node上的数据的
                        if (beforeData.getData() != null) {
                            System.out.println("修改前的数据: " + new String(beforeData.getData()));
                        } else {
                            System.out.println("节点第一次赋值!");
                        }
                        String[] oldPathList= parsePath(beforeData);
                        String[] newPathList= parsePath(afterData);
                        serviceCache.update(oldPathList[1],oldPathList[2],newPathList[2]);
                        System.out.println("修改后的数据: " + new String(afterData.getData()));
                        break;
                    case NODE_DELETED: // 节点删除
                        String[] pathList_d= parsePath(beforeData);
                        if(pathList_d.length<=2) break;
                        else {
                            String serviceName=pathList_d[1];
                            String address=pathList_d[2];
                            //将新注册的服务加入到本地缓存中
                            serviceCache.delete(serviceName,address);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    //解析节点对应地址
    public String[] parsePath(ChildData childData){
        //获取更新的节点的路径
        String path=new String(childData.getPath());
        //按照格式 ，读取
        return path.split("/");
    }

}

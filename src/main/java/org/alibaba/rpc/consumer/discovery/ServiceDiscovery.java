package org.alibaba.rpc.consumer.discovery;

import com.alibaba.fastjson2.JSON;
import org.alibaba.rpc.common.bean.ServiceInfo;
import org.alibaba.rpc.common.bean.ServiceProvider;
import org.alibaba.rpc.common.zk.CuratorClient;

import java.util.ArrayList;
import java.util.List;

public class ServiceDiscovery {
    /**
     * 根据服务信息，默认使用负载均衡算法找到服务提供者
     * 这部分后面需要结合zookeeper实现，这里暂时写死
     * @param serviceInfo 服务信息
     * @return 提供此服务的任意一个服务提供折，使用负载均衡策略
     */
    public static ServiceProvider providerDiscovery(ServiceInfo serviceInfo, String zkConnectString) {
        //TODO 这部分后面从zookeeper中获取
//        List<ServiceProvider> providers = new ArrayList<ServiceProvider>();
//        providers.add(new ServiceProvider("127.0.0.1", 8080));
//
//        //TODO 这部分后面使用负载均衡算法实现
//        ServiceProvider serviceProvider = providers.get(0);

        try {
            CuratorClient curatorClient = new CuratorClient(zkConnectString);
            String zkPath = curatorClient.getZkPath(serviceInfo);
            ServiceProvider serviceProvider = JSON.parseObject(curatorClient.getData(zkPath), ServiceProvider.class);
            return serviceProvider;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据服务信息找到服务提供者
     * 这部分后面需要结合zookeeper实现，这里暂时写死
     * @param serviceInfo 服务信息
     * @param loadBalance 负载均衡算法
     * @return 提供此服务的任意一个服务提供折，使用负载均衡策略
     */
    public static ServiceProvider providerDiscovery(ServiceInfo serviceInfo, LoadBalance loadBalance){
        //TODO 这部分后面从zookeeper中获取
        List<ServiceProvider> providers = new ArrayList<ServiceProvider>();
        providers.add(new ServiceProvider("127.0.0.1", 8080));

        //TODO 这部分后面使用负载均衡算法实现
        ServiceProvider serviceProvider = providers.get(0);

        return serviceProvider;
    }
}

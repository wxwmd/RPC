package org.alibaba.rpc.common.zk;


import org.alibaba.rpc.common.bean.ServiceInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

import java.util.List;

public class CuratorClient {
    private CuratorFramework client;

    private static final String NAMESPACE = "services";

    private static final int SESSION_TIMEOUT = 60 * 1000;

    private static final int CONNECTION_TIMEOUT = 60 * 1000;

    public CuratorClient(String connectString, String namespace, int sessionTimeout, int connectionTimeout) {
        client = CuratorFrameworkFactory.builder().namespace(namespace).connectString(connectString)
                .sessionTimeoutMs(sessionTimeout).connectionTimeoutMs(connectionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10))
                .build();
        client.start();
    }

    public CuratorClient(String connectString, int timeout) {
        this(connectString, NAMESPACE, timeout, timeout);
    }

    public CuratorClient(String connectString) {
        this(connectString, NAMESPACE, SESSION_TIMEOUT, CONNECTION_TIMEOUT);
    }

    public CuratorFramework getClient() {
        return client;
    }

    public void addConnectionStateListener(ConnectionStateListener connectionStateListener) {
        client.getConnectionStateListenable().addListener(connectionStateListener);
    }

    public String createPathData(String path, byte[] data) throws Exception {
        return client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path, data);
    }

    /**
     * 根据service的信息生成存放该service的zk node路径
     * 例如，service是org.alibaba.rpc.service.HelloService，那么其对应的zk node的路径就是/services[NAMESPACE]/org/alibaba/rpc/service/HelloService
     * @param serviceInfo 服务
     * @return 该服务对应的zk node的路径
     */
    public String getZkPath(ServiceInfo serviceInfo){
        return '/' + serviceInfo.getServiceName().replace('.', '/') + '/' + serviceInfo.getVersion();
    }

    public byte[] getData(String path) throws Exception {
        return client.getData().forPath(path);
    }

    public void updatePathData(String path, byte[] data) throws Exception {
        client.setData().forPath(path, data);
    }

    public void deletePath(String path) throws Exception {
        client.delete().forPath(path);
    }

    public void watchNode(String path, Watcher watcher) throws Exception {
        client.getData().usingWatcher(watcher).forPath(path);
    }



    public List<String> getChildren(String path) throws Exception {
        return client.getChildren().forPath(path);
    }

    public void watchTreeNode(String path, TreeCacheListener listener) {
        TreeCache treeCache = new TreeCache(client, path);
        treeCache.getListenable().addListener(listener);
    }

    public void watchPathChildrenNode(String path, PathChildrenCacheListener listener) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);
        //BUILD_INITIAL_CACHE 代表使用同步的方式进行缓存初始化。
        pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        pathChildrenCache.getListenable().addListener(listener);
    }

    public void close() {
        client.close();
    }
}

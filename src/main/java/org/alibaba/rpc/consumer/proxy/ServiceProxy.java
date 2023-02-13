/**
 * 根据client指定的类，返回代理对象
 * 当client调用代理对象内的方法时，会远程调用服务提供者完成实际计算，并将结果通过netty返回给client
 * 因此在client看来，这个方法就像是本地调用一样
 */

package org.alibaba.rpc.consumer.proxy;

import org.alibaba.rpc.consumer.handler.ServiceInvocationHandler;

import java.lang.reflect.Proxy;

public class ServiceProxy {
    public static <T> T createService(Class<T> className, String version){
        return (T) Proxy.newProxyInstance(
                className.getClassLoader(),
                new Class[]{className},
                new ServiceInvocationHandler(className, version)
        );
    }
}

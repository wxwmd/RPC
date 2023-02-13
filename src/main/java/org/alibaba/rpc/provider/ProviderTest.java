package org.alibaba.rpc.provider;

import org.alibaba.rpc.common.bean.ServiceInfo;
import org.alibaba.rpc.common.bean.ServiceProvider;
import org.alibaba.rpc.provider.register.RpcRegister;

public class ProviderTest {

    public static void main(String[] args) {
        RpcRegister.registerService(new ServiceProvider("127.0.0.1", 8081),
                "127.0.0.1:2181",
                new ServiceInfo("org.alibaba.rpc.provider.service.HelloService", "1.0"));
    }
}

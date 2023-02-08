package org.alibaba.rpc.common.bean;

import lombok.Data;

@Data
public class RpcRequest {
    String className;
    String methodName;
    Object[] params;

    public RpcRequest(String className, String methodName, Object[] params){
        this.className = className;
        this.methodName = methodName;
        this.params = params;
    }
}

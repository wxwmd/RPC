package org.alibaba.rpc.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RpcRequest {
    String requestId;
    String className;
    String methodName;
    Object[] params;
}

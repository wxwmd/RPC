package org.alibaba.rpc.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RpcResponse {
    String responseId;
    Object result;
}

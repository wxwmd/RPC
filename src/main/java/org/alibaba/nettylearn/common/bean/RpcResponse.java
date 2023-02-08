package org.alibaba.nettylearn.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RpcResponse {
    Object result;
}

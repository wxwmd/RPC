/**
 * 服务提供方
 */

package org.alibaba.rpc.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceProvider {
    String address;
    int port;
}

/**
 * 服务信息
 */

package org.alibaba.rpc.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceInfo {
    String serviceName;
    String version;
}

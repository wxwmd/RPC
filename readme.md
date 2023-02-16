TODO:
1. 消费方同步/异步获取消费结果 ✅
2. 粘包半包 ✅
2. 线程资源优化✅
    - 在consumer端使用一个通话管理器ChannelManager，关于与所有provider之间的channel，实现多路复用，防止每次与provider通话都需要重新创建channel。
2. 服务提供方将自己的服务注册到zookeeper，消费方到zookeeper寻找✅
3. netty连接终端管理（基于心跳机制IdleHandler）✅
   - provider长时间没用收到channel传来的rpc request，关闭channel
   - consumer长时间没有收到provider传来的rpc response，重新负载均衡选出一台provider进行rpc调用
4. 负载均衡🪲
5. 多种序列化方式🪲
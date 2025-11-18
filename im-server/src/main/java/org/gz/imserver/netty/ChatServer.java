package org.gz.imserver.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.gz.imcommon.config.TcpConfig;
import org.gz.imserver.codec.MessageDecoder;
import org.gz.imserver.codec.MessageEncoder;
import org.gz.imserver.handler.HeartBeatHandler;
import org.gz.imserver.handler.NettyServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author guozhong
 * @date 2025/11/18
 */
public class ChatServer {
    private final static Logger logger = LoggerFactory.getLogger(ChatServer.class);
    EventLoopGroup mainGroup;
    EventLoopGroup subGroup;
    ServerBootstrap server;
    TcpConfig config;
    public ChatServer(TcpConfig config) {
        this.config = config;
        mainGroup = new NioEventLoopGroup(config.getBossThreadSize());
        subGroup = new NioEventLoopGroup(config.getWorkThreadSize());
        server = new ServerBootstrap();
        server.group(mainGroup,subGroup)
                .channel(NioServerSocketChannel.class)
                // 服务端可连接队列大小
                .option(ChannelOption.SO_BACKLOG, 10240)
                // 参数表示允许重复使用本地地址和端口
                .option(ChannelOption.SO_REUSEADDR, true)
                // 是否禁用Nagle算法 简单点说是否批量发送数据 true关闭 false开启。 开启的话可以减少一定的网络开销，但影响消息实时性
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 保活开关2h没有数据服务端会发送心跳包
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //解码
                        ch.pipeline().addLast(new MessageDecoder());
                        //编码
                        ch.pipeline().addLast(new MessageEncoder());
                        //心跳
                        ch.pipeline().addLast(new HeartBeatHandler());
                        //业务处理
                        ch.pipeline().addLast(new NettyServerHandler());
                    }
                });
    }

    public void start(){
        this.server.bind(this.config.getTcpPort());
    }
}

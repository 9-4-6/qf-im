package org.gz.imserver.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.gz.imcommon.exception.BizException;
import org.gz.imserver.codec.MessageDecoder;
import org.gz.imserver.codec.MessageEncoder;
import org.gz.imserver.config.NettyConfig;
import org.gz.imserver.handler.NettyServerHandler;


import java.util.concurrent.TimeUnit;

/**
 * @author guozhong
 * @date 2025/11/18
 */
@Slf4j
public class ChatServer {
    private final NettyConfig nettyConfig;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private volatile boolean isRunning = false;

    // 注入配置
    public ChatServer(NettyConfig nettyConfig) {
        this.nettyConfig = nettyConfig;
        validateConfig(); // 校验配置
    }

    // 初始化并启动 Netty 服务
    public void start() {
        if (isRunning) {
            log.warn("Netty 服务已启动，端口：{}", nettyConfig.getTcpPort());
            return;
        }

        bossGroup = new NioEventLoopGroup(nettyConfig.getBossThreadSize());
        workerGroup = new NioEventLoopGroup(nettyConfig.getWorkThreadSize());

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 服务端可连接队列大小
                    .option(ChannelOption.SO_BACKLOG, 10240)
                    // 参数表示允许重复使用本地地址和端口
                    .option(ChannelOption.SO_REUSEADDR, true)
                    // 是否禁用Nagle算法
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // TCP 保活机制的原理
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast("decoder", new MessageDecoder()) // 自定义解码器
                                    .addLast("encoder", new MessageEncoder()) // 自定义编码器
                                    .addLast("idleHandler", new IdleStateHandler(
                                            nettyConfig.getHeartBeatTime(), 0, 0, TimeUnit.SECONDS
                                    ))
                                    .addLast("businessHandler", new NettyServerHandler()); // 业务处理器
                        }
                    });

            // 绑定端口并同步等待
            ChannelFuture future = bootstrap.bind(nettyConfig.getTcpPort()).sync();
            serverChannel = future.channel();
            isRunning = true;
            log.info("Netty 服务启动成功，端口：{}", nettyConfig.getTcpPort());

            // 监听服务关闭事件
            serverChannel.closeFuture().addListener(f -> {
                log.info("Netty 服务通道关闭");
                shutdown();
            });
        } catch (Exception e) {
            log.error("Netty 服务启动失败", e);
            // 启动失败时释放资源
            shutdown();
        }
    }

    // 优雅关闭服务
    public void shutdown() {
        if (!isRunning){ return;}

        try {
            if (serverChannel != null) {
                serverChannel.close().sync();
            }
        } catch (Exception e) {
            log.warn("Netty 通道关闭异常", e);
        } finally {
            if (workerGroup != null) {workerGroup.shutdownGracefully();}
            if (bossGroup != null) {bossGroup.shutdownGracefully();}
            isRunning = false;
            log.info("Netty 服务已关闭");
        }
    }

    // 校验配置合法性
    private void validateConfig() {
        int port = nettyConfig.getTcpPort();
        if (port < 1 || port > 65535) {
            throw new BizException("无效的 Netty 端口：" + port);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

}

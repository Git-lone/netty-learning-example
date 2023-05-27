package com.sanshengshui.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


/**
 * @author 穆书伟
 * @date 2018年9月18号
 * @description 服务端启动程序
 */
public final class Server {
    public  static void main(String[] args) throws Exception {
        // Configure the server
        // 创建两个EventLoopGroup对象
        // 创建boss线程组 用于服务端接受客户端的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 创建 worker 线程组 用于进行 SocketChannel 的数据读写
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建 ServerBootstrap 对象，ServerBootstrap是服务端启动引导类
            ServerBootstrap b = new ServerBootstrap();
            // 设置使用的EventLoopGroup
            b.group(bossGroup,workerGroup)
                    // 设置要被实例化的为 NioServerSocketChannel 类
                    .channel(NioServerSocketChannel.class)
                    // 设置 NioServerSocketChannel 的处理器，使用了 io.netty.handler.logging.LoggingHandler 类，用于打印服务端的每个事件
                    // handler 是发生在初始化的时候
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 设置连入服务端的 Client 的 SocketChannel 的处理器
                    // childHandler 是客户端连接之后
                    .childHandler(new ServerInitializer());
            // 使用指定的端口设置套接字地址，先调用 #bind(int port) 方法，绑定端口，后调用sync()方法阻塞等待成功，这个过程就是启动服务端
            // sync() 方法的目的是等待异步的socket绑定事件完成，即等待Future事件的完成
            ChannelFuture future = b.bind(8888).sync();
            // 监听服务端关闭，并阻塞等待，
            future.channel()
                    // 先调用 #closeFuture() 方法，监听服务器关闭
                    .closeFuture()
                    // 后调用 ChannelFuture#sync() 方法，阻塞等待成功，注意，此处不是关闭服务器，而是“监听”关闭
                    .sync();
        } finally {
            // 优雅关闭两个 EventLoopGroup 对象
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

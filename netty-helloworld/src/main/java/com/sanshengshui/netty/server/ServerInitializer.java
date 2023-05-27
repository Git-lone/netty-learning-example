package com.sanshengshui.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * ChannelInitializer进行消息的处理，用来设置出站解码器和入站解码器
 * 客户端和服务端之间消息的传递包含特殊字符需要统一编码格式时，在客户端和服务端加上ChannelInitializer继承类，重写initChannel方法，
 * 进行添加相应的设置，传输协议设置，以及相应的业务实现类，设置编码和解码格式
 * */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();

    private static final ServerHandler SERVER_HANDLER = new ServerHandler();


    /**
     * 基于责任链设计模式（Chain of Responsibility）来设计的业务处理器流水线ChannelPipeline，
     * 内部是一个双向链表结构，能够支持动态地添加和删除业务处理器
     * */
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 添加帧限定符来防止粘包现象
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        // 解码和编码，应和客户端一致
        pipeline.addLast(DECODER);
        pipeline.addLast(ENCODER);

        // 业务逻辑实现类
        pipeline.addLast(SERVER_HANDLER);
    }
}

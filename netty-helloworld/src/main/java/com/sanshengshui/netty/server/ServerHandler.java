package com.sanshengshui.netty.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.SimpleChannelInboundHandler;
import java.net.InetAddress;
import java.util.Date;

/**
 * 业务代码，具体的处理逻辑
 * 一个请求的
 *
 * */
@Sharable
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 建立连接时的处理
     * ChannelHandlerContext通道处理器上下文： 作为一个中间角色把Handler和Pipeline联系起来
     *
     * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 为新连接发送庆祝
        ctx.write("来自服务端：欢迎新连接进来的 " + InetAddress.getLocalHost().getHostName() + "!\r\n");
        ctx.write("It is " + new Date() + " now.\r\n");
        ctx.flush();
    }

    /**
     * ⭐⭐⭐业务处理逻辑
     * Server 的回调方法， channelRead表示接收消息
    * */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // Generate and write a response.
        String response;
        boolean close = false;
        if (msg.isEmpty()) {
            response = "你想说什么？.\r\n";
        } else if ("再见".equals(msg.toLowerCase())) {
            response = "欢迎下次连接!\r\n";
            close = true;
        } else {
            response = "你再说'" + msg + "'吗?\r\n";
        }

        ChannelFuture future = ctx.write(response);

        // addListener()在消息发送完后会进行回调，我们再去处理关闭连接等业务处理
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * Server 的回调方法， channelRead表示接收消息
     * channelRead表示接收消息，可以看到msg转换成了ByteBuf，然后打印，也就是把Client传过来的消息打印了一下，你会发现每次打印完后，channelReadComplete也会调用，如果你试着传一个超长的字符串过来，超过1024个字母长度，你会发现channelRead会调用多次，而channelReadComplete只调用一次。
     * 所以因为ByteBuf是有长度限制的，所以超长了，就会多次读取，也就是调用多次channelRead，而channelReadComplete则是每条消息只会调用一次，无论你多长，分多少次读取，只在该条消息最后一次读取完成的时候调用，所以这段代码把关闭Channel的操作放在channelReadComplete里，放到channelRead里可能消息太长了，结果第一次读完就关掉连接了，后面的消息全丢了
     * */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        String name = ctx.name();
        System.out.println("接收到来自客户端的消息：" +name);
        ctx.flush();
    }

    /**
     * 异常处理
     * */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

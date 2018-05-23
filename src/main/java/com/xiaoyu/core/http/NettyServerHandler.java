package com.xiaoyu.core.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoyu.core.context.DispatchStation;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    Logger logger = LoggerFactory.getLogger("NettyServerHandler");
    private final DispatchStation dispatcher;

    public NettyServerHandler(DispatchStation dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = null;
        if (msg instanceof HttpObject) {
            request = (FullHttpRequest) msg;
        }
        logger.info("uri:" + request.uri());
        // for (Entry<String, String> entry : request.headers()) {
        // System.out.println("HEADER: " + entry.getKey() + '=' +
        // entry.getValue() + "\r\n");
        // }

        FullHttpResponse response = dispatcher.dispatch(request);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
        ctx.channel().writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.channel().close();
    }

}

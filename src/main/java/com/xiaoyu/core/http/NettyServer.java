/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.core.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**2017年4月27日下午2:36:30
 * @author xiaoyu
 * @description netty实现的http server
 */
public class NettyServer {

	public static void main(String args[]) throws InterruptedException {
		NettyServer s = new NettyServer();
		s.run(8080);
	}
	public void run(int port) throws InterruptedException {
		NioEventLoopGroup boss = new NioEventLoopGroup();
		NioEventLoopGroup worker = new NioEventLoopGroup();
		
		ServerBootstrap boot = new ServerBootstrap();
		try {
			boot.group(boss, worker)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG,128)
			.childOption(ChannelOption.TCP_NODELAY, true)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();
					p
					//.addLast(new HttpRequestDecoder())
					//.addLast(new HttpResponseEncoder())
					.addLast(new HttpServerCodec())
					.addLast(new HttpObjectAggregator(1024*1024*1))
					.addLast(new HttpContentCompressor())//gzip
					.addLast(new NettyServerHandler());
				}
				 
			});
			
			ChannelFuture f = boot.bind(port).sync();
			f.channel().closeFuture().sync();
		}
		finally {
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
	}
}

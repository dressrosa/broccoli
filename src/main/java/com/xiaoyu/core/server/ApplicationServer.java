/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.core.server;

import java.io.IOException;

import com.xiaoyu.core.context.ApplicationContext;
import com.xiaoyu.core.context.DefaultContext;
import com.xiaoyu.core.http.NettyServer;

/**
 * 2017年4月28日下午5:27:04
 * 
 * @author xiaoyu
 * @description 启动项
 */
public class ApplicationServer {

	private ApplicationContext context;

	private NettyServer nettyServer;

	private static final int DEFAULT_PORT = 8080;

	private int port;

	public void applicationContext(ApplicationContext context) {
		this.context = context;
	}

	public ApplicationServer port(int port) {
		this.port = port;
		return this;
	}

	public ApplicationServer run(int port) {
		this.port = port;
		final ApplicationContext context = this.context;
		try {
			context.init();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			nettyServer = new NettyServer();
			nettyServer.run(this.port);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return this;
	}

	public static void run() {
		final ApplicationContext context = new DefaultContext();
		final NettyServer nettyServer = new NettyServer();
		try {
			context.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			nettyServer.run(DEFAULT_PORT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}

/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.core.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger("ApplicationServer");

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

    private void initContext() {
        if (context == null) {
            context = new DefaultContext();
        }

    }

    public ApplicationServer rootPackage(String packageName) {
        this.initContext();
        context.setRootPackage(packageName);
        try {
            context.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void run(int port) {
        this.port = port;
        final ApplicationContext context = this.context;
        try {
            context.init();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            nettyServer = new NettyServer(context);
            nettyServer.run(this.port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        this.initContext();
        final NettyServer nettyServer = new NettyServer(context);
        try {
            port = DEFAULT_PORT;
            logger.info("server start in port " + port);
            nettyServer.run(port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}

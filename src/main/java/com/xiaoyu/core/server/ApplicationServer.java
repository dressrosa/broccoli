/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.core.server;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoyu.common.utils.ResourceUtil;
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

    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_ROOT = File.separator;

    private int port = -1;

    private String rootPackage = null;

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
        this.rootPackage = packageName;
        return this;
    }

    public void run() {
        this.initContext();
        if (this.rootPackage == null) {
            this.rootPackage = ResourceUtil.rootPackage();
        }
        if (this.rootPackage == null) {
            this.rootPackage = DEFAULT_ROOT;
        }
        context.setRootPackage(rootPackage);
        try {
            context.init();
            final NettyServer nettyServer = new NettyServer(context);
            if (port > 0) {
                logger.info("server start in port " + port);
                nettyServer.run(port);
                return;
            }
            String tport = ResourceUtil.port();
            if (tport != null) {
                port = Integer.valueOf(tport);
            } else {
                port = DEFAULT_PORT;
            }
            if (port > 0) {
                logger.info("server start in port " + port);
                nettyServer.run(port);
            } else {
                throw new Exception("port must be a positive integer");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

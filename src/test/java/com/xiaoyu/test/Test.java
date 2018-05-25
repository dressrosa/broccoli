/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.test;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import com.xiaoyu.core.server.ApplicationServer;

/**
 * @author:xiaoyu 2017年3月22日上午12:12:47
 * @description:测试
 */
public class Test {

    public static void main(String[] args) throws IOException {
        // aop
        // ApplicationContext context = new DefaultContext();
        // context.setRootPackage("com.xiaoyu").init();
        // People p = (People) context.getBean("com.xiaoyu.example.Man");
        // p.sayHello("xiaoyu");
        // http
        ApplicationServer server = new ApplicationServer();
        server
                .rootPackage("com.xiaoyu")
                .run();
    }

}

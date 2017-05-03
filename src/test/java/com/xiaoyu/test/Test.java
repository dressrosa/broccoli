/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.test;

import java.io.IOException;

import com.xiaoyu.core.context.ApplicationContext;
import com.xiaoyu.core.context.DefaultContext;
import com.xiaoyu.core.server.ApplicationServer;
import com.xiaoyu.example.People;
import com.xiaoyu.example.TestController;

/**
 * @author:xiaoyu 2017年3月22日上午12:12:47
 *
 * @description:测试
 */
public class Test {

	public static void main(String[] args) throws IOException {
		// ApplicationContext context = new DefaultContext();
		// context.setRootPackage("com.xiaoyu").init();
		// People p = (People) context.getBean("com.xiaoyu.example.Man");
		// p.sayHello("xiaoyu");
		
		ApplicationServer server = new ApplicationServer();
		server.rootPackage("com.xiaoyu").run(); 
		//System.out.println(TestController.class.getDeclaredMethods()[0].getName());
		 
	}

}

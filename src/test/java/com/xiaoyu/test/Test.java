/**
 * Ψ�п���,��ӹ����
 */
package com.xiaoyu.test;

import java.io.IOException;

import com.xiaoyu.core.context.ApplicationContext;
import com.xiaoyu.core.context.DefaultContext;
import com.xiaoyu.example.People;

/**
 * @author:xiaoyu 2017��3��22������12:12:47
 *
 * @description:����
 */
public class Test {

	public static void main(String[] args) throws IOException {
		ApplicationContext context = new DefaultContext();
		context.setRootPackage("com.xiaoyu").init();
		People p = (People) context.getBean("com.xiaoyu.example.Man");
		p.sayHello("xiaoyu");
	}

}

/**
 * Ψ�п���,��ӹ����
 */
package com.xiaoyu.example;

import com.xiaoyu.config.annotation.bean.Component;

/**
 * @author:xiaoyu 2017��3��22������12:08:42
 *
 * @description:
 */
@Component
public class Man implements People {

	public void sayHello(String name) {
		System.out.println("Hello! " + name);
	}

}

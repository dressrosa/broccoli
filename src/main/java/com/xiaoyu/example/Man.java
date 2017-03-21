/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.example;

import com.xiaoyu.config.annotation.bean.Component;

/**
 * @author:xiaoyu 2017年3月22日上午12:08:42
 *
 * @description:
 */
@Component
public class Man implements People {

	public void sayHello(String name) {
		System.out.println("Hello! " + name);
	}

}

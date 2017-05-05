/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.example;

import com.xiaoyu.aop.bean.MethodProceed;
import com.xiaoyu.config.annotation.aop.After;
import com.xiaoyu.config.annotation.aop.Around;
import com.xiaoyu.config.annotation.aop.Aspect;
import com.xiaoyu.config.annotation.aop.Before;
import com.xiaoyu.config.annotation.aop.PointCut;

/**
 * @author:xiaoyu
 *
 * @description:切面
 */
@Aspect
public class AopAspect2 {

	@PointCut("com.xiaoyu.example.*")
	public void point2() {
	}

	@Before
	public void before2() {
		System.out.println("前置before2");
	}

	@After
	public void after2() {
		System.out.println("后置after2");
	}

	@Around
	public Object around2(MethodProceed mp) {
		System.out.println("环绕前置aroud before2");
		Object o = null;
		try {
			o = mp.proceed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("环绕后置around after2");
		return o;
	}

}

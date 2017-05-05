/**
 * 唯有读书,不庸不扰
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
public class AopAspect1 {

	@PointCut("com.xiaoyu.example.*")
	public void point1() {
	}

	@Before
	public void before1() {
		System.out.println("前置before1");
	}

	@After
	public void after1() {
		System.out.println("后置after1");
	}

	@Around
	public Object around1(MethodProceed mp) {
		System.out.println("环绕前置aroud before1");
		Object o = null;
		try {
			o = mp.proceed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("环绕后置around after1");
		return o;
	}

}

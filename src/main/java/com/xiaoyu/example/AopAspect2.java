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
	public void point() {
	}

	@Before
	public void before() {
		System.out.println("前置1before1");
	}

	@After
	public void after() {
		System.out.println("后置1after1");
	}

	@Around
	public Object around(MethodProceed mp) {
		System.out.println("环绕前置1aroud before1");
		Object o = null;
		try {
			o = mp.proceed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("环绕前置2around after1");
		return o;
	}

}

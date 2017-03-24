/**
 * Ψ�п���,��ӹ����
 */
package com.xiaoyu.example;

import com.xiaoyu.config.annotation.aop.After;
import com.xiaoyu.config.annotation.aop.Around;
import com.xiaoyu.config.annotation.aop.Aspect;
import com.xiaoyu.config.annotation.aop.Before;
import com.xiaoyu.config.annotation.aop.PointCut;
import com.xiaoyu.core.bean.MethodProceed;

/**
 * @author:xiaoyu 2017��3��22������12:09:30
 *
 * @description:���������
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
	public void around(MethodProceed mp) {
		System.out.println("环绕前置1aroud before1");
		try {
			mp.proceed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("环绕前置2around after1");
	}

}
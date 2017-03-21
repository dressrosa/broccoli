/**
 * Ψ�п���,��ӹ����
 */
package com.xiaoyu.core.bean;

import java.lang.reflect.Method;

/**
 * @author:xiaoyu 2017��3��21������10:16:35
 *
 * @description:�൱��spring�����ProceedingsJoinPoint
 */
public class MethodProceed {

	private Object[] args;

	private Method method;

	private Object target;

	public MethodProceed(Object target, Method method, Object[] args) {
		this.args = args;
		this.method = method;
		this.target = target;
	}

	public Object proceed() throws Exception {
		return this.method.invoke(target, args);
	}

	public String methodName() {
		return method.getName();
	}

}

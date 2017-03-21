/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.xiaoyu.config.constant.AopType;
import com.xiaoyu.core.bean.MethodProceed;

/**
 * @author:xiaoyu 2017年3月21日下午10:07:01
 *
 * @description:jdk原生代理
 */
public class JdkProxy implements IProxy {

	public Object getAopProxy(final Object target, final Method m, final AopType type) {
		return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
				new InvocationHandler() {
					@SuppressWarnings("all")
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						Object result = null;
						switch (type) {
						case BEFORE:
							m.invoke(m.getDeclaringClass().newInstance(), m.getParameters());
							result = method.invoke(target, args);
							return result;
						case AFTER:
							result = method.invoke(target, args);
							m.invoke(m.getDeclaringClass().newInstance(), m.getParameters());
							return result;
						case AROUND:
							MethodProceed mp = new MethodProceed(target, method, args);
							Class[] params = m.getParameterTypes();
							if (params.length == 0 || params[0] != MethodProceed.class)
								throw new IllegalArgumentException("the method " + m.getName() + " in "
										+ m.getDeclaringClass().getName() + " need a param of " + MethodProceed.class);
							m.invoke(m.getDeclaringClass().newInstance(), mp);
							return target;
						}
						return target;
					}
				});
	}

}

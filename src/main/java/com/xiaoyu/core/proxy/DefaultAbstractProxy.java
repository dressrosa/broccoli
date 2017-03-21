/**
 * Ψ�п���,��ӹ����
 */
package com.xiaoyu.core.proxy;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.ServiceLoader;

import com.xiaoyu.config.constant.AopType;

/**
 * @author:xiaoyu 2017��3��21������10:28:16
 *
 * @description:Ĭ�ϵĴ���ʵ��
 */
public abstract class DefaultAbstractProxy {

	private static IProxy proxy;

	public static Object getAopProxy(Object target, Method method, AopType type) {
		ServiceLoader<IProxy> loader = ServiceLoader.load(IProxy.class);
		Iterator<IProxy> iter = loader.iterator();
		if (proxy == null && iter.hasNext())
			proxy = iter.next();
		if (proxy == null)
			proxy = new JdkProxy();
		return proxy.getAopProxy(target, method, type);
	}
}

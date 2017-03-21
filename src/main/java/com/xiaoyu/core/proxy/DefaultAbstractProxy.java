/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.core.proxy;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.ServiceLoader;

import com.xiaoyu.config.constant.AopType;

/**
 * @author:xiaoyu 2017年3月21日下午10:28:16
 *
 * @description:默认的代理实现
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

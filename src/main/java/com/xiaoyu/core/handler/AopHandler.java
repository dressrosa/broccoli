/**
 * Ψ�п���,��ӹ����
 */
package com.xiaoyu.core.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.xiaoyu.config.annotation.aop.After;
import com.xiaoyu.config.annotation.aop.Around;
import com.xiaoyu.config.annotation.aop.Aspect;
import com.xiaoyu.config.annotation.aop.Before;
import com.xiaoyu.config.constant.AopType;
import com.xiaoyu.core.proxy.DefaultAbstractProxy;

/**
 * @author:xiaoyu 2017��3��21������10:34:17
 *
 * @description:aopע�⴦����
 */
public class AopHandler implements AnnotationHandler {

	public Object handle(Class<?> annoClass, Object target) {
		if (annoClass == null)
			throw new IllegalArgumentException("the annoClass cannot be null");
		final Annotation[] annos = annoClass.getAnnotations();
		Annotation aspectAnno = null;
		for (Annotation an : annos) {
			if (an instanceof Aspect) {
				aspectAnno = an;
				break;
			}
		}
		if (aspectAnno == null)
			throw new IllegalArgumentException("cannot find annotation \"aspect\"");
		Object proxy = target;
		final Method[] methods = annoClass.getMethods();
		for (Method m : methods) {
			if (m.getAnnotation(Before.class) != null) {
				proxy = DefaultAbstractProxy.getAopProxy(target, m, AopType.BEFORE);
				continue;
			}
			if (m.getAnnotation(After.class) != null) {
				proxy = DefaultAbstractProxy.getAopProxy(proxy, m, AopType.AFTER);
				continue;
			}
			if (m.getAnnotation(Around.class) != null) {
				proxy = DefaultAbstractProxy.getAopProxy(proxy, m, AopType.AROUND);
				continue;
			}
		}

		return proxy;
	}

	public Object handle(Class<?>[] annoClass, Object target) {
		if (annoClass == null)
			throw new IllegalArgumentException("the annoClass cannot be null");
		Object proxy = target;
		Method[] methods;
		for (Class<?> cl : annoClass) {
			methods = cl.getMethods();
			for (Method m : methods) {
				if (m.getAnnotation(Before.class) != null) {
					proxy = DefaultAbstractProxy.getAopProxy(proxy, m, AopType.BEFORE);
					continue;
				}
				if (m.getAnnotation(After.class) != null) {
					proxy = DefaultAbstractProxy.getAopProxy(proxy, m, AopType.AFTER);
					continue;
				}
				if (m.getAnnotation(Around.class) != null) {
					proxy = DefaultAbstractProxy.getAopProxy(proxy, m, AopType.AROUND);
					continue;
				}
			}
		}

		return proxy;
	}

}

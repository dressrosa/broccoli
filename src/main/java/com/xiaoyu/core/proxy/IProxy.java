/**
 * Ψ�п���,��ӹ����
 */
package com.xiaoyu.core.proxy;

import java.lang.reflect.Method;

import com.xiaoyu.config.constant.AopType;

/**
 * @author:xiaoyu 2017��3��21������10:06:15
 *
 * @description:����ӿ�
 */
public interface IProxy {

	public Object getAopProxy(final Object target, final Method m, final AopType type);
}

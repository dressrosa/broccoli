/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.core.proxy;

import java.lang.reflect.Method;

import com.xiaoyu.config.constant.AopType;

/**
 * @author:xiaoyu 2017年3月21日下午10:06:15
 *
 * @description:代理接口
 */
public interface IProxy {

	public Object getAopProxy(final Object target, final Method m, final AopType type);
}

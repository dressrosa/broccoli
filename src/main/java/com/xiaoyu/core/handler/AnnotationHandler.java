/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.core.handler;

/**
 * @author:xiaoyu 2017年3月21日下午10:32:35
 *
 * @description:注解处理器
 */
public interface AnnotationHandler {

	public Object handle(final Class<?> annoClass, final Object target);

	public Object handle(final Class<?>[] annoClass, final Object target);
}

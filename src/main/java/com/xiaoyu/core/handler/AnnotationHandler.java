/**
 * Ψ�п���,��ӹ����
 */
package com.xiaoyu.core.handler;

/**
 * @author:xiaoyu 2017��3��21������10:32:35
 *
 * @description:ע�⴦����
 */
public interface AnnotationHandler {

	public Object handle(final Class<?> annoClass, final Object target);

	public Object handle(final Class<?>[] annoClass, final Object target);
}

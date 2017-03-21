/**
 * Ψ�п���,��ӹ����
 */
package com.xiaoyu.config.annotation.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author:xiaoyu 2017��3��21������9:42:34
 *
 * @description:�е�
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PointCut {

	public String value() default "";
}

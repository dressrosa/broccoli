/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.config.annotation.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author:xiaoyu 2017年3月21日下午9:42:02
 *
 * @description:切面
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Aspect {

}

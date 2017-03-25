/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.config.annotation.bean;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author:xiaoyu 2017年3月21日下午10:05:04
 *
 * @description:注解bean才会被扫描到
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {

}

/**
 * Ψ�п���,��ӹ����
 */
package com.xiaoyu.config.annotation.bean;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author:xiaoyu 2017��3��21������10:05:04
 *
 * @description:ע��bean�Żᱻɨ�赽
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {

}

/**
 * 唯有读书,不庸不扰
 */
package com.xiaoyu.config.annotation.request;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hongyu
 * @date 2018-05
 * @description
 */

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {

    boolean required() default false;

    String name() default "";
}

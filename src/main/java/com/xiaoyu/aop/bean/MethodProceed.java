/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.aop.bean;

import java.lang.reflect.Method;

/**
 * @author:xiaoyu 2017年3月21日下午10:16:35
 * @description:相当于spring里面的ProceedingsJoinPoint
 */
public class MethodProceed {

    private final Object[] args;

    private final Method method;

    private final Object target;

    public MethodProceed(Object target, Method method, Object[] args) {
        this.args = args;
        this.method = method;
        this.target = target;
    }

    public Object proceed() throws Exception {
        // this.method.setAccessible(true);
        return method.invoke(target, args);
    }

    public String methodName() {
        return method.getName();
    }

}

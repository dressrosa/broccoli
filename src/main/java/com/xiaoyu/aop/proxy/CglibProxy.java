/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.aop.proxy;

import java.lang.reflect.Method;

import com.xiaoyu.aop.bean.MethodProceed;
import com.xiaoyu.common.utils.SeesawQueue;
import com.xiaoyu.config.constant.AopType;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 2017年3月23日下午4:51:37
 * 
 * @author xiaoyu
 * @description cglib代理,通过{@link SeesawQueue}来解决cglib无法实现代理叠加的情况
 */
public class CglibProxy implements IProxy {

    /**
     * 右边存储前置通知,左边存储后置通知,这个可以自己决定,用threadlocal存储每次请求的代理
     */
    private static ThreadLocal<SeesawQueue<Method>> local = new ThreadLocal<SeesawQueue<Method>>() {
        @Override
        protected SeesawQueue<Method> initialValue() {
            return new SeesawQueue<>();
        }

    };
    public static int count = 0;
    Enhancer hancer = new Enhancer();

    /*
     * 实现代理叠加 m代表切面类里面的方法
     */
    @Override
    public Object getAopProxy(final Object target, final Method m, final AopType type) {
        /*
         * 通过双眼怒视好久发现,cglib不能实现代理叠加的原因在于她是以继承为基础的原理.
         * 也就是每次获取代理类的时候我们必须要setSuperclass,虽然我不知道怎么去把动态生成的class
         * 给生成文件,然后看看,不过我猜应该是这样的. 因为java是单继承的模型,这样每次生成的代理类根本无法转化成我们想要的bean了.
         * 所以我们在前置和后置通知处理上,没有使用cglib代理,而是把她们放入SeesawQueue里面.直接返回原target
         * 而环绕通知我们每次传来的target都是上次的superClass,所以每次只会代理出一个正常的cglib类,
         * 这样就不会形成cglib多次编译形成的不伦不类的clglib$xxx. class了
         */
        Class<?> cl = null;
        if ("java.lang.Object".equals(target.getClass().getSuperclass().getName())) {
            cl = target.getClass();
        } else {
            cl = target.getClass().getSuperclass();
        }
        hancer.setSuperclass(cl);
        switch (type) {
        case BEFORE:
            local.get().pushRight(m);
            return target;
        case AFTER:
            local.get().pushLeft(m);
            return target;// 这里根本没有代理,只是存入queue中
        case AROUND:
            hancer.setCallback(new MethodInterceptor() {
                @Override
                public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                    final SeesawQueue<Method> s = local.get();
                    Method me = null;
                    try {
                        while ((me = s.offerRight()) != null) {// 执行所有前置通知
                            me.invoke(me.getDeclaringClass().newInstance(), new Object[] {});
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // 执行所有环绕通知
                    Object result = m.invoke(m.getDeclaringClass().newInstance(),
                            new MethodProceed(target, method, args));
                    try {
                        while ((me = s.offerLeft()) != null) {// 执行所有后置通知
                            me.invoke(me.getDeclaringClass().newInstance(), new Object[] {});
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return result;
                }
            });
            return hancer.create();
        }
        return target;

    }

}

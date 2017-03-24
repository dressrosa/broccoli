/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.core.proxy;

import java.lang.reflect.Method;
import com.xiaoyu.common.utils.SeesawQueue;
import com.xiaoyu.config.constant.AopType;
import com.xiaoyu.core.bean.MethodProceed;
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
	 * 右边存储前置通知,左边存储后置通知,这个可以自己决定
	 */
	SeesawQueue<Method> queue = new SeesawQueue<Method>();

	public static int count = 0;
	Enhancer hancer = new Enhancer();

	/*
	 * 实现代理叠加
	 */
	public Object getAopProxy(final Object target, final Method m, final AopType type) {
		/*
		 * 通过双眼怒视好久发现,cglib不能实现代理叠加的原因在于她是以继承为基础的原理.
		 * 也就是每次获取代理类的时候我们必须要setSuperclass,虽然我不知道怎么去吧动态生成的class
		 * 给生成文件,然后看看,我猜应该是这样的. 因为java是单继承的模型,这样每次生成的代理类根本无法转化成我们想要的bean了.
		 * 所以我们在前置和后置通知处理上,没有使用cglib代理,而是把她们放入SeesawQueue里面.直接返回原target
		 * 
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
			queue.pushRight(m);
			return target;
		case AFTER:
			queue.pushLeft(m);
			return target;
		case AROUND:
			hancer.setCallback(new MethodInterceptor() {
				public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
					final SeesawQueue<Method> s = queue;
					Method me = null;
					while ((me = s.offerRight()) != null) {
						try {
							me.invoke(me.getDeclaringClass().newInstance(), new Object[] {});
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// m.invoke(m.getDeclaringClass().newInstance(), new
					// MethodProceed(target, method, args));
					while ((me = s.offerLeft()) != null) {
						try {
							me.invoke(me.getDeclaringClass().newInstance(), new Object[] {});
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					return null;
				}
			});
			return hancer.create();
		}
		return target;

	}

}

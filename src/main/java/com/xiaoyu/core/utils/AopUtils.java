/**
 * Ψ�п���,��ӹ����
 */
package com.xiaoyu.core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.xiaoyu.config.annotation.aop.PointCut;

/**
 * @author:xiaoyu 2017��3��21������10:28:16
 *
 * @description:aop������
 */
public class AopUtils {

	// ��ȡ������ص�������
	public static List<Class<?>> getAspectClass(Map<String, Class<?>> aspectHolder, String name) {
		Iterator<Class<?>> viter = aspectHolder.values().iterator();
		List<Class<?>> cList = new ArrayList<Class<?>>();
		while (viter.hasNext()) {
			Class<?> c = viter.next();
			Method[] methods = c.getMethods();
			for (Method m : methods) {
				if (checkShouldAspect(m, name)) {
					cList.add(c);
				}
			}

		}
		return cList;

	}

	public static boolean checkShouldAspect(Method method, String name) {
		Annotation[] annos = method.getAnnotations();
		for (Annotation a : annos) {
			if (a instanceof PointCut) {
				PointCut p = (PointCut) a;
				Pattern r = Pattern.compile(p.value());
				// �Ƿ�·��ƥ����ͬ
				Matcher m = r.matcher(name);
				for (;;) {
					if (m.find())
						return true;
				}

			}
		}
		return false;

	}
}

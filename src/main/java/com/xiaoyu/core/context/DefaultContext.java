/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.core.context;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import com.xiaoyu.config.annotation.aop.Aspect;
import com.xiaoyu.config.annotation.bean.Component;
import com.xiaoyu.core.handler.AnnotationHandler;
import com.xiaoyu.core.handler.AopHandler;
import com.xiaoyu.core.utils.AopUtils;

/**
 * @author:xiaoyu 2017年3月21日下午11:07:37
 *
 * @description:默认上下文实现
 */
public class DefaultContext implements ApplicationContext {

	private static boolean STARTED = true;

	private String packageName;

	// 存储所有标有component的class
	private static HashMap<String, Class<?>> beanHolder = new HashMap<String, Class<?>>();
	// 存储所有标有aspect的class
	private static HashMap<String, Class<?>> aspectHolder = new HashMap<String, Class<?>>();

	public Object getBean(String name) {
		if (!STARTED)
			throw new IllegalStateException("context not init,please init first");
		try {
			Class<?> c = beanHolder.get(name);
			if (c == null)
				throw new ClassNotFoundException(("cannot find class " + name + ",please check annotation"));
			AnnotationHandler annoHandler = new AopHandler();
			List<Class<?>> aspectList = AopUtils.getAspectClass(aspectHolder, name);
			Object o = annoHandler.handle(aspectList.toArray(new Class[aspectList.size()]), c.newInstance());
			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ApplicationContext setRootPackage(String packgeName) {
		this.packageName = packgeName;
		return this;
	}

	public ApplicationContext init() throws IOException {
		this.init0();
		STARTED = true;
		return this;
	}

	boolean recursive = true;

	private void init0() throws IOException {
		if (packageName == null || "".equals(packageName))
			throw new IllegalArgumentException("packageName cannot be null");
		String packageDir = packageName.replace(".", "/");
		Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDir);
		while (dirs.hasMoreElements()) {
			URL url = dirs.nextElement();
			String proto = url.getProtocol();
			if ("file".equals(proto)) {
				String filePath = URLDecoder.decode(url.getFile(), "utf-8");
				this.recursiveClass(packageName, filePath, recursive);
			}
		}

	}

	private void recursiveClass(String packageName, String path, final boolean recursive) {
		File dir = new File(path);
		if (!dir.exists() || !dir.isDirectory())
			return;
		File[] dirFiles = dir.listFiles(new FileFilter() {

			public boolean accept(File f) {
				return (recursive && f.isDirectory()) || (f.getName().endsWith(".class"));
			}
		});
		String fileName;
		int index;
		String suffix;
		String className;
		try {
			for (final File file : dirFiles) {
				if (file.isDirectory())
					recursiveClass(packageName + "." + file.getName(), file.getAbsolutePath(), recursive);
				else {
					fileName = file.getName();
					index = fileName.indexOf(".");
					suffix = fileName.substring(index);
					if (".class".equals(suffix)) {
						className = fileName.substring(0, index);
						Class<?> c = Thread.currentThread().getContextClassLoader()
								.loadClass(packageName + "." + className);
						Annotation[] annos;
						// 注解类其实也是个interface
						if ((c.isInterface() && c.isAnnotation()) || !c.isInterface() && !c.isEnum()) {
							annos = c.getAnnotations();
							for (Annotation an : annos) {
								if (an instanceof Component) {
									beanHolder.put(packageName + "." + className, c);
									continue;
								}
								if (an instanceof Aspect) {
									aspectHolder.put(packageName + "." + className, c);
									continue;
								}
							}
						}
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}

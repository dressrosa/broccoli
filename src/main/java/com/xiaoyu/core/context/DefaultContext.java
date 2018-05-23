/**
 * 唯有读书,不慵不扰
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

import com.xiaoyu.aop.handler.AopHandler;
import com.xiaoyu.config.annotation.aop.Aspect;
import com.xiaoyu.config.annotation.bean.Component;
import com.xiaoyu.config.annotation.bean.Controller;
import com.xiaoyu.config.annotation.bean.Service;
import com.xiaoyu.core.handler.AnnotationHandler;
import com.xiaoyu.core.utils.AopUtils;

/**
 * @author:xiaoyu 2017年3月21日下午11:07:37
 * @description:默认上下文实现
 */
public class DefaultContext implements ApplicationContext {

    private static boolean STARTED = true;

    private String packageName;

    // 存储所有标有component的class
    protected static HashMap<String, Class<?>> clsHolder = new HashMap<>();
    // 存储所有标有component的class的一个实例
    protected static HashMap<String, Object> singletonHolder = new HashMap<>();
    // 存储所有标有aspect的class
    protected static HashMap<String, Class<?>> aspectHolder = new HashMap<>();
    // 存储class对应的实现类名(全包名)
    protected static HashMap<String, String> implHolder = new HashMap<>();

    @Override
    public Object getBean(String name) {
        if (!STARTED) {
            throw new IllegalStateException("context not init,please init first");
        }
        try {
            Class<?> c = clsHolder.get(name);
            if (c == null) {
                throw new ClassNotFoundException(("cannot find class " + name + ",please check annotation"));
            }
            AnnotationHandler annoHandler = new AopHandler();
            List<Class<?>> aspectList = AopUtils.getAspectClass(aspectHolder, name);
            Object o = annoHandler.handle(aspectList.toArray(new Class[aspectList.size()]), singletonHolder.get(name));
            return o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Class<?> getHandledClass(String name) {
        return this.getBean(name).getClass();
    }

    @Override
    public ApplicationContext setRootPackage(String packgeName) {
        packageName = packgeName;
        return this;
    }

    @Override
    public ApplicationContext init() throws IOException {
        this.init0();
        STARTED = true;
        return this;
    }

    boolean recursive = true;

    private void init0() throws IOException {
        if (packageName == null || "".equals(packageName)) {
            throw new IllegalArgumentException("packageName cannot be null");
        }
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
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirFiles = dir.listFiles(new FileFilter() {

            @Override
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
                if (file.isDirectory()) {
                    this.recursiveClass(packageName + "." + file.getName(), file.getAbsolutePath(), recursive);
                } else {
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
                                if (an instanceof Component || an instanceof Controller || an instanceof Service) {
                                    clsHolder.put(packageName + "." + className, c);
                                    singletonHolder.put(packageName + "." + className, c.newInstance());
                                    if (c.getInterfaces().length > 0) {
                                        implHolder.put(c.getInterfaces()[0].getName(), packageName + "." + className);
                                    }
                                    continue;
                                }
                                //切面类
                                if (an instanceof Aspect) {
                                    aspectHolder.put(packageName + "." + className, c);
                                    continue;
                                }
                            }
                        }

                    }
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

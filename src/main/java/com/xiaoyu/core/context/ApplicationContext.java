/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.core.context;

import java.io.IOException;

/**
 * @author:xiaoyu 2017年3月21日下午11:05:12
 * @description:上下文 加载component
 */
public interface ApplicationContext {

    public Object getBean(String name);

    public Class<?> getHandledClass(String name);

    public ApplicationContext setRootPackage(String packgeName);

    public ApplicationContext init() throws IOException;
}

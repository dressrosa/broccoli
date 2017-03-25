/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.core.context;

import java.io.IOException;

/**
 * @author:xiaoyu 2017年3月21日下午11:05:12
 *
 * @description:上下文 加载component
 */
public interface ApplicationContext {

	public Object getBean(String name);

	public ApplicationContext setRootPackage(String packgeName);

	public ApplicationContext init() throws IOException;
}

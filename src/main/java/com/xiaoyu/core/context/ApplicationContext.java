/**
 * Ψ�п���,��ӹ����
 */
package com.xiaoyu.core.context;

import java.io.IOException;

/**
 * @author:xiaoyu 2017��3��21������11:05:12
 *
 * @description:������ ����component
 */
public interface ApplicationContext {

	public Object getBean(String name);

	public ApplicationContext setRootPackage(String packgeName);

	public ApplicationContext init() throws IOException;
}

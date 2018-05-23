/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.core.handler;

import java.util.ArrayList;
import java.util.List;

/**
 * 2017年4月28日下午4:35:02
 * 
 * @author xiaoyu
 * @description handler处理链
 */
public abstract class HandlerChain {

    protected List<Handler> handlerList = new ArrayList<>();

    public abstract void handleInChain();

}

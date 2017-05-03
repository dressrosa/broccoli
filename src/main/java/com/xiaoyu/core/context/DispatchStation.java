/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.core.context;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoyu.config.annotation.bean.Controller;
import com.xiaoyu.config.annotation.request.RequestMapping;
import com.xiaoyu.core.exception.BrocolliException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * 2017年4月28日下午4:25:00
 * 
 * @author xiaoyu
 * @description 通过访问过来的uri进行分配
 */
public class DispatchStation {

	private static final Logger logger = LoggerFactory.getLogger("DispatchStation");

	private ApplicationContext context;

	public DispatchStation(ApplicationContext context) {
		this.context = context;
		try {
			initUrlMappings();
		} catch (BrocolliException e) {
			e.printStackTrace();
		}
	}

	// 存放所有的requestmapping对应的<url,methodName>
	private static HashMap<String, String> mappingHolder = new HashMap<String, String>();

	/**
	 * 初始化所有的访问映射地址
	 * 
	 * @throws BrocolliException
	 */
	public void initUrlMappings() throws BrocolliException {
		final Collection<Class<?>> valueSet = DefaultContext.beanHolder.values();
		final Iterator<Class<?>> iter = valueSet.iterator();
		while (iter.hasNext()) {
			Class<?> cls = iter.next();
			String url = null;
			String headUrl = null;// 标注在类上面的requestMapping
			if (cls.isAnnotationPresent(Controller.class)) {// 是否标有controller
				if (cls.isAnnotationPresent(RequestMapping.class)) {// 是否标有requestMapping
					headUrl = cls.getAnnotation(RequestMapping.class).value();
					if (!headUrl.startsWith("/") && !headUrl.equals("/"))
						headUrl = "/" + headUrl;
				}
				Method[] methods = cls.getDeclaredMethods();
				if (methods.length == 0) // controller里面啥都没有
					continue;

				RequestMapping rm = null;
				for (Method m : methods) {
					rm = m.getAnnotation(RequestMapping.class);// 方法上的requestMapping
					if (rm == null)
						continue;
					String rmv = rm.value();
					if (rmv != null || rmv != "") {
						if ("/".equals(rmv))
							rmv = null;
						if (!rmv.startsWith("/")) {
							rmv = "/" + rmv;
						}
					}
					if (headUrl == null)
						headUrl = "";
					url = headUrl + rmv;
					if ("".equals(url))
						url = "/";

					logger.info(" the mapping url:" + url);
					if (mappingHolder.containsKey(url)) {
						throw new BrocolliException("the mapping [" + url + "] in the " + cls.getSimpleName() + "."
								+ m.getName() + " exists the same one.");
					}
					mappingHolder.put(url, m.getName());
				}

			}
		}
	}

	private ExecutorService executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
			new LinkedTransferQueue<Runnable>());

	/**
	 * 处理uri,进行分配
	 */
	public FullHttpResponse dispatch(FullHttpRequest request) {
		final String uri = request.uri();
		FullHttpResponse response = null;
		String result = null;
		ByteBuf content = null;
		if (!checkUrlExist(uri)) {
			content = Unpooled.wrappedBuffer("404".getBytes());
			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, content);
			return response;
		}
		result = dispatch0(uri);
		content = Unpooled.wrappedBuffer(result.getBytes());
		response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
		return response;
	}

	private boolean checkUrlExist(String uri) {
		if ("/".equals(uri))
			return mappingHolder.containsKey(uri);
		if (uri.contains("?"))
			return mappingHolder.containsKey(uri.split("\\?")[0]);
		return false;
	}

	private String dispatch0(String uri) {
		final String uri1 = uri;
		Callable<String> task = new Callable<String>() {
			@Override
			public String call() throws Exception {
				return processMethod(uri1);
			}
		};
		Future<String> f = executor.submit(task);
		String result = null;
		try {
			result = f.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	private String processMethod(String uri) {
		if (!uri.contains("?")) {
			String methodName = mappingHolder.get(uri);

		} else {
			String[] arr = uri.split("\\?");

		}
		return "TODO";
	}
}

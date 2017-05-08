/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.core.context;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import com.xiaoyu.core.utils.CommonUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

/**
 * 2017年4月28日下午4:25:00
 * 
 * @author xiaoyu
 * @description 通过访问过来的uri进行分配
 */
public class DispatchStation {

	private static final Logger logger = LoggerFactory.getLogger("DispatchStation");

	// public static void main(String args[ ]){
	// System.out.println(DispatchStation.class.getName());
	// }
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

	private static final String SPLIT_CHAR = ":";

	/**
	 * 初始化所有的访问映射地址
	 * 
	 * @throws BrocolliException
	 */
	public void initUrlMappings() throws BrocolliException {
		final Collection<Class<?>> valueSet = DefaultContext.clsHolder.values();
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
				if (methods.length == 0) { // controller里面啥都没有
					mappingHolder.put(headUrl, null);// 路径存在,但是什么都木有
					continue;
				}

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
					mappingHolder.put(url, cls.getName() + SPLIT_CHAR + m.getName());
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
		logger.info("访问地址:" + uri);
		FullHttpResponse response = null;
		String result = null;
		ByteBuf content = null;
		if (!checkUrlExist(uri)) {
			content = Unpooled.wrappedBuffer("404".getBytes());
			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, content);
			return response;
		}

		if (HttpMethod.GET.equals(request.method())) {
			result = dispatch0(uri, null);
		} else if (HttpMethod.POST.equals(request.method())) {
			HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
			decoder.offer(request);
			List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
			Map<String, Object> map = new HashMap<>();
			for (InterfaceHttpData p : parmList) {
				Attribute data = (Attribute) p;
				try {
					map.put(data.getName(), data.getValue());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			result = dispatch0(uri, map);
		}

		if (result == null) {
			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
			return response;
		}
		content = Unpooled.wrappedBuffer(result.getBytes());
		response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
		return response;
	}

	private boolean checkUrlExist(String uri) {
		if ("/".equals(uri))
			return mappingHolder.containsKey(uri);
		if (uri.contains("?"))
			return mappingHolder.containsKey(uri.split("\\?")[0]);
		return mappingHolder.containsKey(uri);
	}

	private String dispatch0(String uri, Map<String, Object> paramsMap) {
		final String uri1 = uri;
		Callable<String> task = new Callable<String>() {
			@Override
			public String call() throws Exception {
				return (String) processMethod(uri1, paramsMap);

			}
		};
		Future<String> f = executor.submit(task);
		String result = null;
		try {
			result = f.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		return result;
	}

	private Object processMethod(String uri, Map<String, Object> paramsMap) throws BrocolliException {
		// for example /home/bibi?name=123&pwd=234
		if (!uri.contains("?")) {
			String location = mappingHolder.get(uri);
			if (location == null)
				return null;
			String[] str = location.split(SPLIT_CHAR);
			String clsName = str[0];
			String methodName = str[1];
			Method[] methods = DefaultContext.clsHolder.get(clsName).getDeclaredMethods();
			for (Method m : methods) {
				if (m.getName().equals(methodName)) {
					try {
						if (paramsMap == null)// get方法
							return m.invoke(context.getBean(clsName), new Object[] {});
						// post方法
						return m.invoke(context.getBean(clsName), paramsMap.values().toArray());
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			String[] arr = uri.split("\\?");
			String params = arr[1];
			paramsMap = new HashMap<String, Object>();
			String[] paramKv = params.split("&");
			for (int i = 0; i < paramKv.length; i++) {
				String[] parr = paramKv[i].split("=");
				paramsMap.put(parr[0], parr[1]);

			}
			String location = mappingHolder.get(arr[0]);
			if (location == null)
				return null;
			String[] str = location.split(SPLIT_CHAR);
			String clsName = str[0];
			String methodName = str[1];
			Object target = context.getBean(clsName);
			Method[] methods = target.getClass().getDeclaredMethods();

			// 判断参数
			try {
				for (Method m : methods) {
					if (m.getName().equals(methodName)) {
						Object[] pArr = null;
						// 此处依赖于java8
						if (CommonUtils.isJdk8()) {
							Parameter[] para = m.getParameters();
							pArr = new Object[para.length];
							for (int i = 0; i < para.length; i++) {
								if (para[i].isNamePresent()) {
									if (paramsMap.containsKey(para[i])) {
										pArr[i] = paramsMap.get(para[i]);
										continue;
									}
								} else {
									// 此处只能严格依赖参数的传入顺序了
									pArr = paramsMap.values().toArray();
									break;
								}
								// 正常是肯定有参数的
								throw new BrocolliException("the param " + para[i].getName() + " missed.");
							}
						} else {
							pArr = paramsMap.values().toArray();
						}

						return m.invoke(target, pArr);
					}
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}

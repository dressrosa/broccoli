/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.core.context;

import java.io.IOException;
import java.lang.reflect.Field;
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

import com.xiaoyu.common.utils.MethodParamsResolver;
import com.xiaoyu.config.annotation.bean.Autowired;
import com.xiaoyu.config.annotation.bean.Controller;
import com.xiaoyu.config.annotation.request.RequestMapping;
import com.xiaoyu.config.annotation.request.RequestParam;
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
import io.netty.handler.codec.http.multipart.MixedFileUpload;
import io.netty.util.internal.StringUtil;

/**
 * 2017年4月28日下午4:25:00
 * 
 * @author xiaoyu
 * @description 通过访问过来的uri进行分配
 */
public class DispatchStation {

    private static final Logger logger = LoggerFactory.getLogger("DispatchStation");

    private final ApplicationContext context;

    /**
     * 解析method的参数
     */
    private static final MethodParamsResolver resolver = new MethodParamsResolver();
    /**
     * 用于开启进程执行一次请求
     */
    private final ExecutorService executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
            new LinkedTransferQueue<Runnable>());

    public DispatchStation(ApplicationContext context) {
        this.context = context;
        try {
            this.initUrlMappings();
        } catch (BrocolliException e) {
            e.printStackTrace();
        }
    }

    // 存放所有的requestmapping对应的<url,methodName>
    private static HashMap<String, String> mappingHolder = new HashMap<>();

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
            // 标注在类上面的requestMapping
            String headUrl = null;
            // 是否标有controller
            if (cls.isAnnotationPresent(Controller.class)) {
                // 初始化成员变量
                this.initFields(cls);
                // 是否标有requestMapping
                if (cls.isAnnotationPresent(RequestMapping.class)) {
                    headUrl = cls.getAnnotation(RequestMapping.class).value();
                    if (!headUrl.startsWith("/") && !headUrl.equals("/")) {
                        headUrl = "/" + headUrl;
                    }
                }
                Method[] methods = cls.getDeclaredMethods();
                // controller里面啥都没有
                if (methods.length == 0) {
                    // 路径存在,但是什么都木有
                    mappingHolder.put(headUrl, null);
                    continue;
                }

                RequestMapping rm = null;
                for (Method m : methods) {
                    // 方法上的requestMapping
                    rm = m.getAnnotation(RequestMapping.class);
                    if (rm == null) {
                        continue;
                    }
                    String rmv = rm.value();
                    if (rmv != null && rmv != "") {
                        if ("/".equals(rmv)) {
                            rmv = "";
                        }
                        if (!rmv.startsWith("/")) {
                            rmv = "/" + rmv;
                        }
                    }
                    if (headUrl == null) {
                        headUrl = "";
                    }
                    url = headUrl + rmv;
                    if ("".equals(url)) {
                        url = "/";
                    }

                    logger.info("Controller->{} with the requestMapping->{}", cls.getSimpleName(), url);
                    if (mappingHolder.containsKey(url)) {
                        throw new BrocolliException("the mapping [" + url + "] in the " + cls.getSimpleName() + "."
                                + m.getName() + " exists the same one.");
                    }
                    mappingHolder.put(url, cls.getName() + SPLIT_CHAR + m.getName());
                }

            }
        }
    }

    /**
     * 对类里面的注入类进行递归初始化(注入类的实现类里面可能也有注入类),
     */
    private void initFields(Class<?> cls) {
        String fieldName = null;
        try {
            for (Field f : cls.getDeclaredFields()) {
                fieldName = f.getName();
                // 有自动注入的,进行注入
                if (f.isAnnotationPresent(Autowired.class)) {
                    if (!f.isAccessible()) {
                        f.setAccessible(true);
                    }
                    Class<?> target = DefaultContext.clsHolder
                            .get(DefaultContext.implHolder.get(f.getType().getName()));
                    if (target != null) {
                        this.initFields(target);
                        f.set(DefaultContext.singletonHolder.get(cls.getName()),
                                DefaultContext.singletonHolder.get(target.getName()));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("cannot autowired the filed->{}", fieldName, e);
        }
    }

    /**
     * 处理uri,进行分配
     */
    public FullHttpResponse dispatch(FullHttpRequest request) {
        final String uri = request.uri();
        logger.info("request url->{}", uri);
        FullHttpResponse response = null;
        String result = null;
        ByteBuf content = null;
        if (!this.checkUrlExist(uri)) {
            content = Unpooled.wrappedBuffer("404".getBytes());
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, content);
            return response;
        }

        if (HttpMethod.GET.equals(request.method())) {
            result = this.dispatch0(uri, null);
        } else if (HttpMethod.POST.equals(request.method())) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
            decoder.offer(request);
            List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
            Map<String, Object> map = new HashMap<>();
            // httpRequest
            map.put("request", request);
            try {
                for (InterfaceHttpData p : parmList) {
                    if (p instanceof Attribute) {
                        Attribute data = (Attribute) p;
                        map.put(data.getName(), data.getValue());
                    } else if (p instanceof MixedFileUpload) {
                        // 处理多媒体文件
                        MixedFileUpload data = (MixedFileUpload) p;
                        map.put(data.getName(), data);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            result = this.dispatch0(uri, map);
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
        if ("/".equals(uri)) {
            return mappingHolder.containsKey(uri);
        }
        if (uri.contains("?")) {
            return mappingHolder.containsKey(uri.split("\\?")[0]);
        }
        return mappingHolder.containsKey(uri);
    }

    private String dispatch0(String uri, Map<String, Object> paramsMap) {
        final String uri1 = uri;
        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return (String) DispatchStation.this.processMethod(uri1, paramsMap);

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
            if (location == null) {
                return null;
            }
            String[] str = location.split(SPLIT_CHAR);
            String clsName = str[0];
            String methodName = str[1];
            Method[] methods = DefaultContext.clsHolder.get(clsName).getDeclaredMethods();
            for (Method m : methods) {
                if (m.getName().equals(methodName)) {
                    try {
                        if (paramsMap == null) {
                            return m.invoke(context.getBean(clsName), new Object[] {});
                        }
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
            String[] paramKv = params.split("&");
            for (int i = 0; i < paramKv.length; i++) {
                String[] parr = paramKv[i].split("=");
                if (parr.length == 1) {
                    // 传了个空参数
                    paramsMap.put(parr[0], null);
                } else {
                    paramsMap.put(parr[0], parr[1]);
                }

            }
            String location = mappingHolder.get(arr[0]);
            if (location == null) {
                return null;
            }
            String[] str = location.split(SPLIT_CHAR);
            String clsName = str[0];
            String methodName = str[1];
            // 获取bean
            Object target = context.getBean(clsName);
            Method[] methods = target.getClass().getSuperclass().getDeclaredMethods();

            // 判断参数
            try {
                for (Method m : methods) {
                    Parameter[] para = m.getParameters();
                    if (!m.getName().equals(methodName)) {
                        continue;
                    }
                    if (para.length == 0) {
                        return m.invoke(target, new Object[0]);
                    }
                    Object[] pArr = null;
                    String argsNames[] = resolver.getParameterNames(m);
                    if (argsNames.length == para.length) {
                        pArr = doResolveAndMatchParams(argsNames, m, paramsMap);
                    }
                    // 此处依赖于java8
                    else if (CommonUtils.isJdk8() && para[0].isNamePresent()) {
                        pArr = doResolveAndMatchParams(m, paramsMap);
                    } else {
                        // 此处只能严格依赖参数的传入顺序了
                        logger.warn(
                                "cannot match params correctly,recommend to add @RequestParam with name in method->{}",
                                m.getName());
                        pArr = paramsMap.values().toArray();
                    }
                    return m.invoke(target, pArr);
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

    private Object[] doResolveAndMatchParams(Method m, Map<String, Object> paramsMap) throws BrocolliException {
        Parameter[] para = m.getParameters();
        Object[] pArr = new Object[para.length];
        Object pValue = null;
        for (int i = 0; i < para.length; i++) {
            if (paramsMap.containsKey(para[i].getName())) {
                pValue = paramsMap.get(para[i].getName());
                if (pValue == null) {
                    pValue = paramsMap.get(para[i].getName());
                    if (pValue == null) {
                        // 检查是否有@requestParam
                        pValue = doCheckRequestParam(para[i], para[i].getName(), paramsMap);
                    }
                } else {
                    pArr[i] = pValue;
                }
            } else {
                // 检查是否有@requestParam
                pValue = doCheckRequestParam(para[i], para[i].getName(), paramsMap);
            }
            if (pValue != null) {
                pArr[i] = pValue;
            }
        }
        return pArr;

    }

    private Object[] doResolveAndMatchParams(final String[] argsNames, Method m, Map<String, Object> paramsMap)
            throws BrocolliException {
        Parameter[] para = m.getParameters();
        Object[] pArr = new Object[argsNames.length];
        Object pValue = null;
        for (int i = 0; i < argsNames.length; i++) {
            // 检查是否有@requestParam
            pValue = doCheckRequestParam(para[i], argsNames[i], paramsMap);
            if (pValue == null) {
                if (paramsMap.containsKey(argsNames[i])) {
                    pValue = paramsMap.get(argsNames[i]);
                } else {
                    pValue = doCheckSpecialParam(para[i], paramsMap);
                }
            }
            if (pValue != null) {
                pArr[i] = pValue;
            }
        }
        return pArr;
    }

    /**
     * 是否是特殊参数 比如httprequest
     * 
     * @param parameter
     * @return
     */
    private Object doCheckSpecialParam(Parameter parameter, Map<String, Object> paramsMap) {
        System.out.println(parameter.getType().getSimpleName());
        if ("FullHttpRequest".equals(parameter.getType().getSimpleName())) {
            return paramsMap.get("request");
        }
        return null;
    }

    /**
     * 检查是否有@requestParam
     * 
     * @param pa
     * @param paName
     * @param paramsMap
     * @return
     * @throws BrocolliException
     */
    private Object doCheckRequestParam(Parameter pa, String paName, Map<String, Object> paramsMap)
            throws BrocolliException {
        // 检查是否有@requestParam
        RequestParam rp = pa.getAnnotation(RequestParam.class);
        if (rp != null) {
            if (!StringUtil.isNullOrEmpty(rp.name())) {
                Object pValue1 = paramsMap.get(rp.name());
                if (rp.required() && pValue1 == null) {
                    throw new BrocolliException(
                            "the param " + paName + " required.");
                }
                return pValue1;
            }
        }
        return null;
    }

}

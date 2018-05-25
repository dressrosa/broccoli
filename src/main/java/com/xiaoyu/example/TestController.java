/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.example;

import com.xiaoyu.config.annotation.bean.Autowired;
import com.xiaoyu.config.annotation.bean.Controller;
import com.xiaoyu.config.annotation.request.RequestMapping;
import com.xiaoyu.config.annotation.request.RequestParam;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.MixedFileUpload;

@Controller
@RequestMapping("home")
public class TestController {

    @Autowired
    private ITestService testService;

    @RequestMapping("bibi")
    public String hello(FullHttpRequest req, String name, @RequestParam(required = true) String pwd,
            MixedFileUpload photo) {
        System.out.println("pwd:" + pwd);
        System.out.println(testService == null);
        return testService.hello(name);
    }
}

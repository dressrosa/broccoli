/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.example;

import com.xiaoyu.config.annotation.bean.Autowired;
import com.xiaoyu.config.annotation.bean.Controller;
import com.xiaoyu.config.annotation.request.RequestMapping;
import com.xiaoyu.config.annotation.request.RequestParam;

@Controller
@RequestMapping("home")
public class TestController {

    @Autowired
    private ITestService testService;

    @RequestMapping("bibi")
    public String hello(String name, @RequestParam(required = true) String pwd) {
        System.out.println("pwd:" + pwd);
        System.out.println(testService == null);
        return testService.hello(name);
    }
}

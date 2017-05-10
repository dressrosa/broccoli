/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.example;

import com.xiaoyu.config.annotation.bean.Autowired;
import com.xiaoyu.config.annotation.bean.Controller;
import com.xiaoyu.config.annotation.request.RequestMapping;

@Controller
@RequestMapping("home")
public class TestController {

	@Autowired
	private ITestService testService;

	@RequestMapping("bibi")
	public String hello(String name, String pwd) {
		System.out.println("pwd:" + name);
		System.out.println(testService == null);
		return testService.hello(name);
	}
}

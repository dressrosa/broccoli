/**
 * 唯有读书,不庸不扰
 */
package com.xiaoyu.example;

import com.xiaoyu.config.annotation.bean.Controller;
import com.xiaoyu.config.annotation.request.RequestMapping;

@Controller
@RequestMapping("home")
public class TestController {

	@RequestMapping("bibi")
	public String hello(String name,String pwd){
		System.out.println("nihoa");
		return "nihaoa "+name+":"+pwd;
	}
}

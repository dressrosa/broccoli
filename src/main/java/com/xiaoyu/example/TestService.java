/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.example;

import com.xiaoyu.config.annotation.bean.Autowired;
import com.xiaoyu.config.annotation.bean.Service;

@Service
public class TestService implements ITestService {

    @Autowired
    private IPeopleService peopleService;

    @Override
    public String hello(String name) {
        return "你好啊," + peopleService.getFullName(name);
    }

}

/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.example;

import com.xiaoyu.config.annotation.bean.Service;

@Service
public class PeopleService implements IPeopleService {

	@Override
	public String getFullName(String name) {
		return "尼古拉.费德罗." + name;
	}

}

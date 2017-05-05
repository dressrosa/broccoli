/**
 * 唯有读书,不庸不扰
 */
package com.xiaoyu.core.utils;

import java.util.regex.Pattern;

public class CommonUtils {

	public static boolean isJdk8() {
		return Pattern.matches("1.8.*", System.getProperty("java.version"));
	}

	public static boolean isJdk7() {
		return Pattern.matches("1.7.*", System.getProperty("java.version"));
	}
}

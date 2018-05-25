/**
 * 
 */
package com.xiaoyu.common.utils;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author hongyu
 * @date 2018-05
 * @description
 */
public class ResourceUtil {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("application", Locale.getDefault(),
            Thread.currentThread().getContextClassLoader());

    public static String getString(String key) {
        final ResourceBundle tbundle = bundle;
        return tbundle.getString(key);
    }

    public static String port() {
        final ResourceBundle tbundle = bundle;
        try {
            return tbundle.getString("application.port");
        } catch (MissingResourceException e) {
            return null;
        }
    }

    public static String rootPackage() {
        final ResourceBundle tbundle = bundle;
        try {
            return tbundle.getString("application.root");

        } catch (MissingResourceException e) {
            return null;
        }
    }
}

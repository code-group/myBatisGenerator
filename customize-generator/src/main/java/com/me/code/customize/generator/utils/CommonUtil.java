package com.me.code.customize.generator.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author zhull
 * @date 2018/6/13
 * <P>公用工具</P>
 */
public class CommonUtil {

    public static String PACKAGE = "package $modulePackagePath;";
    public static String IMPORT = "import $modulePackagePath;";
    public static String SPACE4 = "    ";
    public static String PRI = "PRI";

    /**
     * 将下划线分割字符串转为驼峰
     * @param name
     * @return
     */
    public static String toHump(String name) {
        if (StringUtils.isEmpty(name)) {
            return name;
        }
        StringBuilder sb = new StringBuilder();
        String[] strs = name.split("_");
        for (String str : strs) {
            sb.append(str.substring(0, 1).toUpperCase()).append(str.toLowerCase().substring(1));
        }
        return sb.toString();
    }

    /**
     * 将首字母变为小写
     * @param name
     * @return
     */
    public static String toLowerHead(String name) {
        if (StringUtils.isEmpty(name)) {
            return name;
        }
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    /**
     * 将首字母变为大写
     * @param name
     * @return
     */
    public static String toUpperHead(String name) {
        if (StringUtils.isEmpty(name)) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * 首字母小写.
     *
     * @param oriStr 原字符串
     * @return 首字母大写后的字符串
     */
    public static String firstCharToLower(final String oriStr) {
        final StringBuilder sb = new StringBuilder(oriStr);
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * 获取空格
     * @param  n tab数目
     * @return
     */
    public static String getNTab(int n) {
        if (n <= 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; ++i) {
                sb.append(SPACE4);
            }
            return sb.toString();
        }
    }
}

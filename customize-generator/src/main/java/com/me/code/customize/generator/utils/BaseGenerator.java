package com.me.code.customize.generator.utils;

import java.util.List;

/**
 * @author zhull
 * @date 2018/6/13
 * <P></P>
 */
public class BaseGenerator {

    static String AUTHOR;

    public static void setAUTHOR(String author) {
        AUTHOR = author;
    }
    /**
     * 添加引包语句
     *
     * @param packageName 包名
     * @param content     output
     */
    static void addPackage(String packageName, List<String> content) {
        content.add(CommonUtil.PACKAGE.replace("$modulePackagePath", packageName));
    }
}

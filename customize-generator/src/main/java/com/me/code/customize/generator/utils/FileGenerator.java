package com.me.code.customize.generator.utils;

import com.me.code.customize.generator.data.ConfigInfo;
import com.me.code.customize.generator.data.TableInfo;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.List;

/**
 * @author zhull
 * @date 2018/6/12
 * <P>文件生成器</P>
 */
public class FileGenerator {

    private static String baseDirectory = ClassLoader.getSystemResource("").getPath().replace("target/classes/", "src/main/");

    public static void generate(ConfigInfo configInfo, List<TableInfo> tableInfos) throws IOException {
        if (StringUtils.isEmpty(configInfo.getModulePackagePath()) || tableInfos == null || tableInfos.size() == 0) {
            throw new RuntimeException("必传参数为空");
        }
        for (TableInfo tableInfo : tableInfos) {
            System.out.println("正在处理\t\t" + tableInfo.getTableName());
            List<String> content = DomainGenerator.generate(configInfo, tableInfo);
            writeToFile(baseDirectory + "java/" + configInfo.getDomainPackagePath().replace(".", "/")
                    + "/" + tableInfo.getClassName() + configInfo.getDomainFilePostfix(), content);

            content = DaoGenerator.generate(configInfo, tableInfo);
            writeToFile(baseDirectory + "java/" + configInfo.getModulePackagePath().replace(".", "/")
                    + "/" + tableInfo.getClassName() + configInfo.getDaoFilePostfix(), content);

            content = MapperGenerator.generate(configInfo, tableInfo);
            writeToFile(baseDirectory + configInfo.getMapperDir() + "/" + tableInfo.getClassName() +
                    configInfo.getMapperFilePostfix(), content);
            System.out.println("已完成\t\t" + tableInfo.getTableName());
        }
    }

    /**
     * 写文件
     *
     * @param filePathName
     * @param content
     */
    private static void writeToFile(String filePathName, List<String> content) throws IOException {
        if (StringUtils.isEmpty(filePathName)) {
            throw new RuntimeException("文件名为空");
        }
        String filePath = filePathName.substring(0, filePathName.lastIndexOf("/"));
        File directory = new File(filePath);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("创建文件夹失败");
        }
        File file = new File(filePathName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
             BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
            if (null != content) {
                for (String line : content) {
                    bufferedWriter.write(line == null ? "" : line);
                    bufferedWriter.newLine();
                }
            }
            bufferedWriter.flush();
        }
    }

    public static void main(String[] args) {
        System.out.println(baseDirectory);
    }
}

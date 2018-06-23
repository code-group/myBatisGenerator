package com.me.code.customize.generator;

import com.me.code.customize.generator.data.ConfigInfo;
import com.me.code.customize.generator.data.TableInfo;
import com.me.code.customize.generator.jdbc.DbUtil;
import com.me.code.customize.generator.jdbc.SchemaUtil;
import com.me.code.customize.generator.utils.BaseGenerator;
import com.me.code.customize.generator.utils.FileGenerator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhull
 * @date 2018/6/13
 * <P>Mybatis生成器</P>
 */
public class MybatisGenerator {

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        // 设置作者名
        BaseGenerator.setAUTHOR("ali");
        DbUtil.initConnection("jdbc:mysql://localhost/test_db", "root", "123456");
        // 生成数据库中所有的表对应的dao文件
//        List<TableInfo> tableInfos = SchemaUtil.getTableInfos("test_db", null);
        // 生成数据库中指定表对应的dao文件
        List<TableInfo> tableInfos = SchemaUtil.getTableInfos("test_db", new ArrayList<String>(){{add("USER");add("ORDER_INFO");}});

        ConfigInfo configInfo = new ConfigInfo();
        configInfo.setModulePackagePath("com.me.test.dao");
        configInfo.setDomainPackageName("domain");
        configInfo.setDomainPostfix("Po");
        configInfo.setDaoPostfix("Dao");
        configInfo.setMapperPostfix("Mapper");
        configInfo.setMapperDir("resources/test");
        FileGenerator.generate(configInfo, tableInfos);
        System.out.println("~~~~~~~~~~~~全部完成~~~~~~~~~~~~");
    }
}

package org.mybatis.generator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.internal.DefaultCommentGenerator;

/**
 * @author: ali
 * @date: 2017/8/22
 * @description: 注释生成器
 */
public class MyCommentGenerator extends DefaultCommentGenerator {

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn){
        // 添加字段注释
        StringBuffer sb = new StringBuffer();
        field.addJavaDocLine("/**");
        field.addJavaDocLine(" * <pre>");
        if(introspectedColumn.getRemarks() != null) {
            field.addJavaDocLine(" * " + introspectedColumn.getRemarks());
        }
        sb.append(" * 表字段： ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append(".");
        sb.append(introspectedColumn.getActualColumnName());
        field.addJavaDocLine(sb.toString());
        field.addJavaDocLine(" * </pre>");
        field.addJavaDocLine(" * ");
        field.addJavaDocLine(" */");
    }
    @Override
    public void addGeneralMethodComment(Method method,
                                        IntrospectedTable introspectedTable) {

    }

}

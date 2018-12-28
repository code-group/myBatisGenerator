package com.me.code.customize.generator.data;

import com.me.code.customize.generator.utils.CommonUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author zhull
 * @date 2018/6/12
 * <P>表信息</P>
 */
@Data
public class TableInfo {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表注释
     */
    private String comment;

    /**
     * 类名(首字母大写)
     */
    private String className;

    /**
     * 对象名(首字母小写)
     */
    private String objectName;

    /**
     * 列属性
     */
    private List<ColumnInfo> columnInfos;

    /**
     * 唯一索引列属性
     */
    private List<ColumnInfo> uniqueColumnInfos;

    public void setClassName() {
        this.className = CommonUtil.toHump(this.tableName);
    }

    public void setObjectName() {
        if (StringUtils.isEmpty(this.className)) {
            this.setClassName();
        }
        this.objectName = CommonUtil.toLowerHead(this.className);
    }
}

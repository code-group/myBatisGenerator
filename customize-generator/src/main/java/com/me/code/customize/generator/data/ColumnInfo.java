package com.me.code.customize.generator.data;

import com.me.code.customize.generator.utils.CommonUtil;
import lombok.Data;

/**
 * @author zhull
 * @date 2018/6/12
 * <P>列信息</P>
 */
@Data
public class ColumnInfo {

    /**
     * 列名
     */
    private String columnName;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 注释
     */
    private String comment;

    /**
     * 键
     */
    private String key;

    /**
     * 属性名
     */
    private String property;

    /**
     * 数据类型枚举
     */
    private DataTypeTranslator.DataType type;

    public void setProperty() {
        this.property = CommonUtil.toLowerHead(CommonUtil.toHump(this.columnName));
    }

    public void setType() {
        this.type = DataTypeTranslator.DataType.instanceOf(this.dataType);
        if (this.type == null) {
            throw new RuntimeException("未识别的数据类型: " + this);
        }
    }
}

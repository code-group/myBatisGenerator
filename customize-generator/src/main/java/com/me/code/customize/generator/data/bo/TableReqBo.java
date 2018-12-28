package com.me.code.customize.generator.data.bo;

import lombok.Data;

import java.util.List;

/**
 * @author zhull
 * @date 2018/12/26
 * <P>主程序请求参数</P>
 */
@Data
public class TableReqBo {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 唯一索引列表
     */
    private List<String> uniqueKeys;
}

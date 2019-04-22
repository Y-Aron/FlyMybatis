package org.aron.mybatis.jdbc.bean;

import lombok.Data;

/**
 * @author: Y-Aron
 * @create: 2019-01-13 23:07
 **/
@Data
public class SqlObject {
    private String sql;
    private Object[] args;
    private Class<?> resultType;
    public SqlObject(String sql, Class<?> resultType) {
        this.sql = sql;
        this.resultType = resultType;
    }
}

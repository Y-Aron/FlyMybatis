package org.aron.mybatis.jdbc.bean;

import lombok.Data;
import org.aron.mybatis.jdbc.Environment;

/**
 * @author: Y-Aron
 * @create: 2019-03-26 23:22
 */
@Data
public class JDBCBean {
    private String driverClass;
    private String pool;
    private String url;
    private String username;
    private String password;
    private int initSize = Environment.INIT_SIZE;
    private int minSize = Environment.MIN_SIZE;
    private int maxSize = Environment.MAX_SIZE;
    private boolean autoCommit;
}

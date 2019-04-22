package org.aron.mybatis.jdbc;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.aron.mybatis.core.transaction.Transaction;
import org.aron.mybatis.core.transaction.impl.JDBCTransaction;
import org.aron.mybatis.jdbc.bean.JDBCBean;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author: Y-Aron
 * @create: 2019-01-15 10:49
 **/
@Slf4j
public class Environment {
    @Getter
    @Setter
    private boolean primary;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private DataSource dataSource;

    @Getter
    @Setter
    private Transaction transaction;

    private JDBCBean jdbc;

    public static final int INIT_SIZE = 1;

    public static final int MIN_SIZE = 1;

    public static final int MAX_SIZE = 100;

    public Environment(String name) {
        this.name = name;
    }

    public Environment(String name, Transaction transaction, DataSource dataSource) throws SQLException {
        this.name = name;
        this.dataSource = dataSource;
        this.transaction = transaction;
        initialize();
    }

    public void initialize() throws SQLException {
        this.primary = true;
        if (this.transaction == null) {
            this.transaction = new JDBCTransaction();
        }
        if (this.jdbc != null) {
            this.dataSource = DataSourceFactory.build(this.jdbc);
            this.transaction.setDataSource(dataSource, true);
        }
    }

    public void setJdbcProperty(String key, String value) {
        if (this.jdbc == null) {
            jdbc = new JDBCBean();
        }
        // 设置数据源驱动
        if ("driver.class".equalsIgnoreCase(key)) {
            jdbc.setDriverClass(value);
        }
        // 设置数据源URL
        if ("url".equalsIgnoreCase(key)) {
            jdbc.setUrl(value);
        }
        // 设置数据源用户名
        if ("username".equalsIgnoreCase(key)) {
            jdbc.setUsername(value);
        }
        // 设置数据源密码
        if ("password".equalsIgnoreCase(key)) {
            jdbc.setPassword(value);
        }
        // 设置数据源连接池
        if ("pool".equalsIgnoreCase(key)) {
            jdbc.setPool(value);
        }
        // 设置数据源连接初始大小
        if ("init.size".equalsIgnoreCase(key)) {
            jdbc.setInitSize(NumberUtils.toInt(value, INIT_SIZE));
        }
        // 设置数据源连接最小值
        if ("min.size".equalsIgnoreCase(key)) {
            jdbc.setMinSize(NumberUtils.toInt(value, MIN_SIZE));
        }
        // 设置数据源连接最大值
        if ("max.size".equalsIgnoreCase(key)) {
            jdbc.setMaxSize(NumberUtils.toInt(value, MAX_SIZE));
        }
        if ("auto.commit".equalsIgnoreCase(key)) {
            jdbc.setAutoCommit(BooleanUtils.toBoolean(value));
        }
    }
}

package org.aron.mybatis.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.aron.mybatis.jdbc.bean.JDBCBean;

import javax.sql.DataSource;

/**
 * @author: Y-Aron
 * @create: 2019-01-21 10:55
 **/
@Slf4j
public class DataSourceFactory {

    private static DataSource buildHiKariCPDataSource(JDBCBean jdbcBean) {
        return getDataSource(jdbcBean);
    }

    public static DataSource getDataSource(JDBCBean jdbcBean) {
        HikariDataSource hikari = new HikariDataSource();
        hikari.setAutoCommit(jdbcBean.isAutoCommit());
        hikari.setPoolName(jdbcBean.getPool());
        hikari.setJdbcUrl(jdbcBean.getUrl());
        hikari.addDataSourceProperty("cachePrepStmts", "true");
        hikari.addDataSourceProperty("prepStmtCacheSize", "250");
        hikari.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikari.setUsername(jdbcBean.getUsername());
        hikari.setPassword(jdbcBean.getPassword());
        hikari.setDriverClassName(jdbcBean.getDriverClass());
        hikari.setMaximumPoolSize(jdbcBean.getMaxSize());
        hikari.setMinimumIdle(jdbcBean.getMinSize());
        return hikari;
    }

    public static DataSource build(JDBCBean jdbcBean) {
        log.debug("----------构建DataSource----------");
        log.debug("jdbc config: {}", jdbcBean);
        log.debug("----------构建DataSource完毕！----------");
        return buildHiKariCPDataSource(jdbcBean);
    }
}

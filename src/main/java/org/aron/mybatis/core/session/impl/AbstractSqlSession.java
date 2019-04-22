package org.aron.mybatis.core.session.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aron.mybatis.core.Configuration;
import org.aron.mybatis.core.executor.Executor;
import org.aron.mybatis.core.executor.impl.JDBCExecutor;
import org.aron.mybatis.core.session.SqlSession;
import org.aron.mybatis.core.transaction.Transaction;
import org.aron.mybatis.jdbc.Environment;
import org.aron.mybatis.jdbc.MappedState;
import org.aron.mybatis.proxy.MapperProxyFactory;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Y-Aron
 * @create: 2019-01-15 09:18
 **/
@Slf4j
public abstract class AbstractSqlSession implements SqlSession {

    protected final Configuration configuration;

    protected Environment environment;

    private Map<Method, MappedState> cacheMapper;

    protected Transaction transaction;

    protected Executor executor;

    protected AbstractSqlSession(Configuration configuration, Environment environment) {
        this.configuration = configuration;
        this.environment = environment;
        this.transaction = this.environment.getTransaction();
        this.cacheMapper = new ConcurrentHashMap<>(0);
        this.executor = new JDBCExecutor(this.transaction);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setEnvironment(String name) throws SQLException {
        if (!StringUtils.equals(name, this.environment.getName())) {
            Environment environment = this.configuration.getEnvironment(name);
            if (environment == null) {
                throw new SQLException("environment["+ name +"] is not exists");
            }
            this.environment = environment;
            this.transaction.setDataSource(environment.getDataSource(), true);
        }
    }

    @Override
    public void setAutoCommit(boolean autoCommit) {
        this.transaction.setAutoCommit(autoCommit);
    }

    @Override
    public boolean isAutoCommit() {
        return this.transaction.isAutoCommit();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.transaction.getConnection();
    }

    @Override
    public void commit() throws SQLException {
        this.transaction.commit();
    }

    @Override
    public void rollback() throws SQLException {
        this.transaction.rollback();
    }

    @Override
    public void transaction() throws SQLException {
        this.transaction.transaction();
    }

    @Override
    public void setDataSource(DataSource dataSource, boolean rst) throws SQLException {
        this.transaction.setDataSource(dataSource, rst);
        this.environment.setDataSource(dataSource);
    }

    @Override
    public void setDataSource(DataSource dataSource) throws SQLException {
        this.setDataSource(dataSource, false);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <T> T getMapper(Class<T> clazz) throws SQLException {
        return MapperProxyFactory.getProxyInstance(clazz, this);
    }

    @Override
    public MappedState getMappedStatement(Method method) {
        return cacheMapper.get(method);
    }

    @Override
    public void registerMapper(Method method, MappedState mapped) {
        if (!cacheMapper.containsKey(method)) {
            cacheMapper.put(method, mapped);
        }
    }

    @Override
    public int insert(String sql, Object... args) throws SQLException {
        return this.executor.insert(sql, args);
    }

    @Override
    public int delete(String sql, Object... args) throws SQLException {
        return this.executor.delete(sql, args);
    }

    @Override
    public int update(String sql, Object... args) throws SQLException {
        return this.executor.update(sql, args);
    }

    @Override
    public <T> List<T> select(String sql, Class<T> clazz, Object... args) throws SQLException {
        return this.executor.select(sql, clazz, args);
    }

    @Override
    public <T> T selectOne(String sql, Class<T> clazz, Object... args) throws SQLException {
        return this.executor.selectOne(sql, clazz, args);
    }

    @Override
    public <K, V> Map<K, V> selectOne(String sql, Object... args) throws SQLException {
        return this.executor.selectOne(sql, args);
    }

    @Override
    public <K, V> List<Map<K, V>> select(String sql, Object... args) throws SQLException {
        return this.executor.select(sql, args);
    }
}

package org.aron.mybatis.core.session;

import org.aron.mybatis.core.Configuration;
import org.aron.mybatis.core.transaction.Transaction;
import org.aron.mybatis.jdbc.Environment;
import org.aron.mybatis.jdbc.MappedState;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author: Y-Aron
 * @create: 2019-01-13 01:00
 **/
public interface SqlSession extends Transaction {

    // 获取mapper代理类
    <T> T getMapper(Class<T> clazz) throws SQLException;

    // 获取缓存得mapper状态类
    MappedState getMappedStatement(Method method);

    void registerMapper(Method method, MappedState mapped);

    // 配置数据环境
    void setEnvironment(Environment environment);

    void setEnvironment(String name) throws SQLException;

    // 获取配置信息
    Configuration getConfiguration();

    // 提交事务
    void commit() throws SQLException;

    // 回滚事务
    void rollback() throws SQLException;

    // 开启事务
    void transaction() throws SQLException;

    // 查询语句 指定对象类型
    <T> List<T> select(String sql, Class<T> clazz, Object... args) throws SQLException;

    // 单条查询语句 指定对象类型
    <T> T selectOne(String sql, Class<T> clazz, Object... args) throws SQLException;

    // 单条查询语句 不指定对象类型
    <K, V> Map<K, V> selectOne(String sql, Object... args) throws SQLException;

    // 多条查询语句 不指定对象类型
    <K, V> List<Map<K, V>> select(String sql, Object... args) throws SQLException;

    // 插入操作
    int insert(String sql, Object... args) throws SQLException;

    // 修改操作
    int update(String sql, Object... args) throws SQLException;

    // 删除操作
    int delete(String sql, Object... args) throws SQLException;
}

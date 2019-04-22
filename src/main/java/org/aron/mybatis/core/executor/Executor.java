package org.aron.mybatis.core.executor;

import org.aron.mybatis.core.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author: Y-Aron
 * @create: 2019-01-13 19:57
 **/
public interface Executor {

    Transaction getTransaction();

    // 查询sql语句 指定对象类型
    <T> List<T> select(String sql, Class<T> clazz, Object... args) throws SQLException;

    // 单条查询sql语句 指定对象类型
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

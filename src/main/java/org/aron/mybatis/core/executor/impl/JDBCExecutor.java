package org.aron.mybatis.core.executor.impl;

import lombok.extern.slf4j.Slf4j;
import org.aron.mybatis.core.transaction.Transaction;
import org.aron.mybatis.jdbc.bean.StatementBean;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.aron.mybatis.jdbc.handler.ResultSetHandler.*;

/**
 * @author: Y-Aron
 * @create: 2019-01-15 10:13
 **/
@Slf4j
public class JDBCExecutor extends AbstractExecutor {

    public JDBCExecutor(Transaction transaction) {
        super(transaction);
    }

    @Override
    public <T> List<T> select(String sql, Class<T> clazz, Object... args) throws SQLException {
        // 带参数查询
        StatementBean statementBean = null;
        try {
            statementBean = query(sql, args);
            //noinspection unchecked
            return (List<T>) handler(statementBean.getResultSet(), clazz);
        } catch (SQLException e) {
            // 回滚事务
            this.transaction.rollback();
            throw e;
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        } finally {
            this.close(statementBean);
        }
    }

    @Override
    public <T> T selectOne(String sql, Class<T> clazz, Object... args) throws SQLException {
        StatementBean statementBean = null;
        try {
            statementBean = query(sql, args);
            //noinspection unchecked
            return (T) handlerOne(statementBean.getResultSet(), clazz);
        } catch (SQLException e) {
            this.transaction.rollback();
            throw e;
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        } finally {
            this.close(statementBean);
        }
    }

    @Override
    public <K, V> Map<K, V> selectOne(String sql, Object... args) throws SQLException {
        StatementBean statementBean = null;
        try {
            statementBean = query(sql, args);
            //noinspection unchecked
            return (Map<K, V>) handlerOne(statementBean.getResultSet());
        } catch (SQLException e) {
            this.transaction.rollback();
            throw e;
        } finally {
            this.close(statementBean);
        }
    }

    @Override
    public <K, V> List<Map<K, V>> select(String sql, Object... args) throws SQLException {
        StatementBean statementBean = null;
        try {
            statementBean = query(sql, args);
            return handlerMap(statementBean.getResultSet());
        } catch (SQLException e) {
            this.transaction.rollback();
            throw e;
        } finally {
            this.close(statementBean);
        }
    }

    @Override
    public int insert(String sql, Object... args) throws SQLException {
        return execute(sql, args);
    }

    @Override
    public int update(String sql, Object... args) throws SQLException {
        return execute(sql, args);
    }

    @Override
    public int delete(String sql, Object... args) throws SQLException {
        return execute(sql, args);
    }

}

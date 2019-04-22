package org.aron.mybatis.core.executor.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aron.mybatis.core.executor.Executor;
import org.aron.mybatis.core.transaction.Transaction;
import org.aron.mybatis.jdbc.bean.StatementBean;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author: Y-Aron
 * @create: 2019-01-13 20:36
 **/
@Slf4j
public abstract class AbstractExecutor implements Executor {

    @Getter
    protected final Transaction transaction;


    protected AbstractExecutor(Transaction transaction) {
        this.transaction = transaction;
    }

    protected int execute(String sql, Object[] args) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = initParameters(sql, args);
            int row = pstmt.executeUpdate();
            if (transaction.isAutoCommit()) {
                transaction.commit();
                return row;
            } else {
                return 0;
            }
        } catch (SQLException e) {
            this.transaction.rollback();
            throw e;
        } finally {
            this.close(pstmt);
        }
    }

    /**
     * 一个PreparedStatement对应一个ResultSet
     * 数据转换结束后要及时关闭ResultSet
     */
    protected StatementBean query(String sql, Object[] args) throws SQLException {
        PreparedStatement pstmt = initParameters(sql, args);
        return new StatementBean() {{
            setPreparedStatement(pstmt);
            setResultSet(pstmt.executeQuery());
        }};
    }

    protected void close(StatementBean statement) throws SQLException {
        if (statement != null) {
            statement.close();
        }
    }

    protected void close(PreparedStatement preparedStatement) throws SQLException {
        if (preparedStatement != null) {
            preparedStatement.close();
        }
    }

    private PreparedStatement initParameters(String sql, Object[] args) throws SQLException {
        PreparedStatement pstmt = this.transaction.getConnection().prepareStatement(sql);
        for (int i = 0; i < args.length; i++) {
            pstmt.setObject((i+1), args[i]);
        }
        return pstmt;
    }
}


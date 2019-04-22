package org.aron.mybatis.core.transaction.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: Y-Aron
 * @create: 2019-01-15 10:51
 **/
@Slf4j
public class JDBCTransaction extends AbstractTransaction {

    private Connection connection;

    @Setter
    private DataSource dataSource;

    @Getter
    private boolean startTransaction;

    @Override
    public void setDataSource(DataSource dataSource, boolean rst) throws SQLException {
        this.dataSource = dataSource;
        this.connection = getConnection(rst);
    }

    public JDBCTransaction() { }

    private boolean isValid() throws SQLException {
        return connection != null && !connection.isClosed() && connection.isValid(3000);
    }

    private Connection getConnection(boolean initialize) throws SQLException {
        if (!isValid() || initialize) {
            if (dataSource == null) {
                throw new SQLException(DataSource.class + " can not be null");
            }
            this.connection = dataSource.getConnection();
            this.connection.setAutoCommit(autoCommit);
        }
        return this.connection;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(false);
    }

    @Override
    public void commit() throws SQLException {
        if (isValid() && !autoCommit) {
            connection.commit();
            connection.close();
            this.startTransaction = false;
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (isValid() && autoCommit) {
            connection.rollback();
            connection.close();
        }
    }

    @Override
    public void transaction() throws SQLException {
        if (!isValid()) {
            connection = getConnection();
        }
        this.startTransaction = true;
        connection.setAutoCommit(false);
    }

    @Override
    public boolean isAutoCommit() {
        return !this.startTransaction;
    }

}

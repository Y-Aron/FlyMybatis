package org.aron.mybatis.core.transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: Y-Aron
 * @create: 2019-01-15 10:35
 **/
public interface Transaction {

    Connection getConnection() throws SQLException;

    void setAutoCommit(boolean autoCommit);

    void setDataSource(DataSource dataSource) throws SQLException;

    void setDataSource(DataSource dataSource, boolean rst) throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;

    void transaction() throws SQLException;

    boolean isAutoCommit();

}

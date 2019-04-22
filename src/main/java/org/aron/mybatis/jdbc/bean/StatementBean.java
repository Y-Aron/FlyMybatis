package org.aron.mybatis.jdbc.bean;

import lombok.Data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author: Y-Aron
 * @create: 2019-04-09 15:02
 */
@Data
public class StatementBean {
    private PreparedStatement preparedStatement;
    private Statement statement;
    private ResultSet resultSet;


    public void close() throws SQLException {
        if (this.preparedStatement != null) {
            preparedStatement.close();
        }
        if (statement != null) {
            statement.close();
        }
        if (resultSet != null) {
            resultSet.close();
        }
    }
}

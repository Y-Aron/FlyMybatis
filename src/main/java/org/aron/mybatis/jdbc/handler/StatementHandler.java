package org.aron.mybatis.jdbc.handler;

import lombok.extern.slf4j.Slf4j;
import org.aron.mybatis.core.session.SqlSession;
import org.aron.mybatis.jdbc.MappedState;
import org.aron.mybatis.jdbc.bean.ResultType;

import java.sql.SQLException;

/**
 * @author: Y-Aron
 * @create: 2019-01-13 00:14
 **/
@Slf4j
public class StatementHandler {

    private SqlSession sqlSession;

    public StatementHandler(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public Object selectHandler(String sql, MappedState mapped) throws SQLException {
        ResultType resultType = mapped.getResultType();
        switch (resultType) {
            case ONE_CLASS:
                return sqlSession.selectOne(sql, mapped.getClazz(), mapped.getParameters().toArray());

            case LIST_ONE_CLASS:
                return sqlSession.select(sql, mapped.getClazz(), mapped.getParameters().toArray());

            case LIST_MAP_CLASS:
                return sqlSession.select(sql, mapped.getParameters().toArray());

            case MAP_NOT_CUSTOM_CLASS:
                return sqlSession.selectOne(sql, mapped.getParameters().toArray());

            default:
                throw new SQLException("method parameters do not match");
        }
    }

    public Object insertHandler(String sql, MappedState mapped) throws SQLException {
        ResultType resultType = mapped.getResultType();

        if (resultType == ResultType.ONE_CLASS) {
            return sqlSession.insert(sql, mapped.getParameters().toArray());
        }
        throw new SQLException("method parameters do not match");
    }

    public Object updateHandler(String sql, MappedState mapped) throws SQLException {
        ResultType resultType = mapped.getResultType();

        if (resultType == ResultType.ONE_CLASS) {
            return sqlSession.update(sql, mapped.getParameters().toArray());
        }
        throw new SQLException("method parameters do not match");
    }

    public Object deleteHandler(String sql, MappedState mapped) throws SQLException {

        ResultType resultType = mapped.getResultType();

        if (resultType == ResultType.ONE_CLASS) {
            return sqlSession.delete(sql, mapped.getParameters().toArray());
        }
        throw new SQLException("method parameters do not match");
    }
}

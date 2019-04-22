package org.aron.mybatis.proxy.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aron.mybatis.core.session.SqlSession;
import org.aron.mybatis.jdbc.MappedState;
import org.aron.mybatis.jdbc.handler.StatementHandler;

import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * @author: Y-Aron
 * @create: 2019-01-15 12:38
 **/
@Slf4j
public abstract class AbstractProxyHandler implements ProxyHandler {

    protected final Class<?> clazz;
    protected final SqlSession sqlSession;
    private final StatementHandler statementHandler;

    public AbstractProxyHandler(Class<?> clazz, SqlSession sqlSession) {
        this.clazz = clazz;
        this.sqlSession = sqlSession;
        this.statementHandler = new StatementHandler(sqlSession);
    }

    @Override
    public Object executeSql(Method method, Object[] args) throws SQLException {
        MappedState mapped = sqlSession.getMappedStatement(method);
        if (null == mapped) {
            mapped = new MappedState(method, args);
            mapped.buildMapped();
            sqlSession.registerMapper(method, mapped);
        } else {
            mapped.buildMapped(args);
        }
        String sql = mapped.getSql();
        log.debug("sql: {}", sql);
        log.debug("parameters: {}", mapped.getParameters());
        log.debug("result type: {}", mapped.getResultType());

        if (StringUtils.isBlank(sql)) {
            throw new SQLException("sql is not blank");
        }
        switch (mapped.getSqlType()) {
            case SELECT:
                return statementHandler.selectHandler(sql, mapped);
            case INSERT:
                return statementHandler.insertHandler(sql, mapped);
            case UPDATE:
                return statementHandler.updateHandler(sql, mapped);
            case DELETE:
                return statementHandler.deleteHandler(sql, mapped);
        }
        throw new SQLException("The SQL statement type does not match");
    }
}

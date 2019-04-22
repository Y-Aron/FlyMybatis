package org.aron.mybatis.core.session;

import org.apache.commons.lang3.StringUtils;
import org.aron.mybatis.core.Configuration;
import org.aron.mybatis.core.session.impl.DefaultSqlSession;

/**
 * @author: Y-Aron
 * @create: 2019-01-13 01:00
 **/
public class SqlSessionFactory {

    private Configuration configuration;

    private ThreadLocal<SqlSession> localSqlSession;

    public SqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
        this.localSqlSession = new ThreadLocal<>();
    }

    public SqlSession openSession() throws NoSuchFieldException {
        return openSession(false);
    }

    public SqlSession openSession(String environment) throws NoSuchFieldException {
        return openSession(true, environment);
    }

    public SqlSession openSession(boolean initialize) throws NoSuchFieldException {
        return openSession(initialize, null);
    }

    public SqlSession openSession(boolean initialize, String environment) throws NoSuchFieldException {
        if (configuration == null) {
            throw new NoSuchFieldException(Configuration.class.getName() + " is not empty");
        }
        SqlSession sqlSession = localSqlSession.get();
        if (null == sqlSession || initialize) {
            if (StringUtils.isBlank(environment)) {
                environment = configuration.getPrimary();
            }
            sqlSession = new DefaultSqlSession(configuration, configuration.getEnvironment(environment));
            localSqlSession.set(sqlSession);
        }
        return sqlSession;
    }
}

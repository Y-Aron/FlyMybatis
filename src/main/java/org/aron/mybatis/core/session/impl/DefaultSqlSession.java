package org.aron.mybatis.core.session.impl;

import org.aron.mybatis.core.Configuration;
import org.aron.mybatis.jdbc.Environment;

/**
 * @author: Y-Aron
 * @create: 2019-03-26 23:12
 */
public class DefaultSqlSession extends AbstractSqlSession {

    public DefaultSqlSession(Configuration configuration, Environment environment) {
        super(configuration, environment);
    }
}

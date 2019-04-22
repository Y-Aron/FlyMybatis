package org.aron.mybatis.proxy.handler;

import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * @author: Y-Aron
 * @create: 2019-03-27 00:00
 */
public interface ProxyHandler {

    Object getProxyInstance();

    Object executeSql(Method method, Object[] args) throws SQLException;
}

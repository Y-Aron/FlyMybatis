package org.aron.mybatis.proxy;

import org.aron.mybatis.core.session.SqlSession;
import org.aron.mybatis.proxy.handler.impl.CglibInvocationHandler;
import org.aron.mybatis.proxy.handler.impl.JdkInvocationHandler;

import java.sql.SQLException;

/**
 * @author: Y-Aron
 * @create: 2019-01-12 18:11
 **/
public class MapperProxyFactory {

    /**
     * 根据Class类获取代理对象
     * 不执行实例方法
     * @param clazz 被代理类的Class
     * @return 代理类
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxyInstance(final Class<T> clazz, final SqlSession sqlSession) throws SQLException {
        if (!clazz.isInterface()) {
            return null;
        }
        // cglib代理
        if ("cglib".equals(sqlSession.getConfiguration().getProxyMode())) {
            return (T) new CglibInvocationHandler(clazz, sqlSession).getProxyInstance();
        }
        // 默认jdk代理
        return (T) new JdkInvocationHandler(clazz, sqlSession).getProxyInstance();
    }
}

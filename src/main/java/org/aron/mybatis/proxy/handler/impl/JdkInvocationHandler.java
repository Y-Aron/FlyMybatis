package org.aron.mybatis.proxy.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.aron.mybatis.core.session.SqlSession;
import org.aron.mybatis.proxy.handler.AbstractProxyHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author: Y-Aron
 * @create: 2019-01-12 18:59
 **/
@Slf4j
public class JdkInvocationHandler extends AbstractProxyHandler implements InvocationHandler {

    public JdkInvocationHandler(Class<?> clazz, SqlSession sqlSession) {
        super(clazz, sqlSession);
    }

    public Object getProxyInstance() {
        if (this.clazz != null) {
            return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        }
        return null;
    }

    /**
     * 当调用对象中的每个方法时都通过以下的方法执行，对象的方法必须在接口中定义
     * @param proxy 被代理后的对象
     * @param method 将要被指定的方法信息
     * @param args 执行方法时需要的参数
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        boolean objMethod = method.getDeclaringClass().equals(Object.class);
        if (objMethod) {
            return method.invoke(this, args);
        }
        log.trace("----------Jdk代理开始事务----------");
        // 根据方法体和参数执行sql
        Object result = this.executeSql(method, args);
        log.trace("----------Jdk代理结束事务----------");
        return result;
    }
}

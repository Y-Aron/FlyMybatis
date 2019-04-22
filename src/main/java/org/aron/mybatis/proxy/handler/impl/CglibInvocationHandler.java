package org.aron.mybatis.proxy.handler.impl;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.aron.mybatis.core.session.SqlSession;
import org.aron.mybatis.proxy.handler.AbstractProxyHandler;

import java.lang.reflect.Method;

/**
 * @author: Y-Aron
 * @create: 2019-01-12 19:00
 **/
@Slf4j
public class CglibInvocationHandler extends AbstractProxyHandler implements MethodInterceptor {

    public CglibInvocationHandler(Class<?> clazz, SqlSession sqlSession) {
        super(clazz, sqlSession);
    }

    public Object getProxyInstance() {
        // 创建加强器，用来创建态代理
        Enhancer enhancer = new Enhancer();
        // 为加强器指定需要代理的业务类
        enhancer.setSuperclass(clazz);
        // 设置回调：对于代理类上所有方法的调用，都会调用CallBack
        enhancer.setCallback(this);
        return enhancer.create();
    }

    /**
     * @param object 代理类
     * @param method 代理方法
     * @param args 方法参数
     * @param proxy 使用它调用父类的方法
     */
    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        boolean objMethod = method.getDeclaringClass().equals(Object.class);
        if (objMethod) {
            return proxy.invokeSuper(object, args);
        }
        log.trace("----------cglib代理开始事务----------");
        Object result = this.executeSql(method, args);
        log.trace("----------cglib代理结束事务----------");
        return result;
    }
}

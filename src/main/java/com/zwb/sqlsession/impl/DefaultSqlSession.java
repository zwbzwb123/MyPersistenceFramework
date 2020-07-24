package com.zwb.sqlsession.impl;

import com.zwb.UserMapper;
import com.zwb.pojo.Configuration;
import com.zwb.pojo.MappedStatement;
import com.zwb.sqlsession.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    public <T> List<T> selectList(String sid, Object... params) {
        SimpleExecutor executor = new SimpleExecutor();
        return executor.query(configuration,configuration.getMappedStatements().get(sid),params);
    }

    public <T> T selectOne(String id, Object... params) {
        List<Object> objects = selectList(id, params);
        if (objects.size() == 1) {
            return (T) objects.get(0);
        } else {
            throw new RuntimeException("查询异常，请检查参数");
        }
    }


    public  <T> T getMapper(final Class<?> clazz){
        final Configuration configuration = this.configuration;
        Object proxyInstance = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {

            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                String className = clazz.getName();
                String methodName = method.getName();
                if (method.getReturnType() == List.class) {
                    return selectList(className+"."+methodName,args);
                } else {
                    return selectOne(className+"."+methodName,args);
                }
            }
        });
        return (T)proxyInstance;
    }

}

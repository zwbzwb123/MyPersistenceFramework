package com.zwb.sqlsession.impl;

import com.zwb.pojo.Configuration;
import com.zwb.sqlsession.SqlSession;
import com.zwb.sqlsession.SqlSessionFactory;

public class DefaultSqlSessionFactory implements SqlSessionFactory {


    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration){
        this.configuration = configuration;
    }

    public SqlSession openSession(){
        return new DefaultSqlSession(configuration);
    }
}

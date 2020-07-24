package com.zwb.sqlsession;


import com.zwb.config.XmlReader;
import com.zwb.pojo.Configuration;
import com.zwb.sqlsession.impl.DefaultSqlSessionFactory;

import java.io.InputStream;

public class SqlSessionFactoryBuilder {

    public static SqlSessionFactory build(InputStream is) {
        XmlReader reader = new XmlReader();
        Configuration configuration = reader.getConfiguration(is);
        SqlSessionFactory factory = new DefaultSqlSessionFactory(configuration);
        return factory;
    }

}

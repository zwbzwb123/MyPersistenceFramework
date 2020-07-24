package com.zwb;


import com.zwb.io.XmlResource;
import com.zwb.sqlsession.SqlSession;
import com.zwb.sqlsession.SqlSessionFactoryBuilder;
import com.zwb.sqlsession.SqlSessionFactory;

import java.io.InputStream;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        InputStream resource = XmlResource.getResource("SqlConfiguration.xml");
        SqlSessionFactory factory = SqlSessionFactoryBuilder.build(resource);
        SqlSession sqlSession = factory.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> select = mapper.select();
        System.out.println(select.get(0).getUsername());
    }
}

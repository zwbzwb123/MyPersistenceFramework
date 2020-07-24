package com.zwb.sqlsession;

public interface SqlSession {

    <T> T getMapper(Class<?> clazz);
}

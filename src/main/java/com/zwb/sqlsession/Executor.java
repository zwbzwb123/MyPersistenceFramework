package com.zwb.sqlsession;

import com.zwb.pojo.Configuration;
import com.zwb.pojo.MappedStatement;

import java.util.List;

public interface Executor {

    <E> List<E> query(Configuration configuration,MappedStatement mappedStatement,Object... params);
}

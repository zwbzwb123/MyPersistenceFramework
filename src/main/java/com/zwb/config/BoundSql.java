package com.zwb.config;

import java.util.HashMap;

public class BoundSql {

    private HashMap<Integer,String> paramsMapping = new HashMap<Integer, String>();

    private Integer paramsIndex = 1;

    private String sql;

    public void put(String params){
        paramsMapping.put(paramsIndex++,params);
    }

    public void storeSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public HashMap<Integer, String> getParamsMapping() {
        return paramsMapping;
    }

    public void setParamsMapping(HashMap<Integer, String> paramsMapping) {
        this.paramsMapping = paramsMapping;
    }
}

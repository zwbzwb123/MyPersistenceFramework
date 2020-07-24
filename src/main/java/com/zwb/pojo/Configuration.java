package com.zwb.pojo;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class Configuration {

    private DataSource ds;

    private Map<String, MappedStatement> mappedStatements;


    public DataSource getDataSource() {
        return ds;
    }

    public void setDataSource(DataSource ds) {
        this.ds = ds;
    }

    public Map<String, MappedStatement> getMappedStatements() {
        return mappedStatements;
    }

    public void setMappedStatements(Map<String, MappedStatement> mappedStatements) {
        this.mappedStatements = mappedStatements;
    }
}

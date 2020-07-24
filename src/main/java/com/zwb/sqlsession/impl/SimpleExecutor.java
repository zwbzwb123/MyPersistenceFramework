package com.zwb.sqlsession.impl;

import com.zwb.config.BoundSql;
import com.zwb.pojo.Configuration;
import com.zwb.pojo.MappedStatement;
import com.zwb.sqlsession.Executor;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleExecutor implements Executor {

    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) {
        List<E> res = new ArrayList<E>();
        try {
            // get connection
            Connection connection = configuration.getDataSource().getConnection();

            String sql = mappedStatement.getSql();

            BoundSql boundSql = parseSql(sql);

            PreparedStatement ps = connection.prepareStatement(boundSql.getSql());

            HashMap<Integer, String> paramsMapping = boundSql.getParamsMapping();

            String paramType = mappedStatement.getParamType();
            if ("".equals(paramType) || paramType == null) {
                for (Map.Entry<Integer, String> entry : paramsMapping.entrySet()) {
                    int i = entry.getKey();
                    ps.setObject(i, params[i - 1]);
                }
            } else {
                Class<?> clazz = Class.forName(paramType);
                if (clazz != null) {
                    for (int i = 1; i <= paramsMapping.size(); i++) {
                        String content = paramsMapping.get(i);
                        Field declaredField = clazz.getDeclaredField(content);
                        declaredField.setAccessible(true);
                        Object o = declaredField.get(params[0]);
                        ps.setObject(i,o);
                    }
                }
            }

            // execute sql
            ResultSet resultSet = ps.executeQuery();

            // return result set
            String resultType = mappedStatement.getResultType();
            Class<?> resultTypeClass = Class.forName(resultType);

            if (resultType == null || "".equals(resultType)) {
                throw new RuntimeException("please check your result type");
            }
            while (resultSet.next()) {
                Object o = resultTypeClass.newInstance();
                ResultSetMetaData metaData = resultSet.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount() ; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object object = resultSet.getObject(columnName);

                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName,resultTypeClass);
                    propertyDescriptor.getWriteMethod().invoke(o,object);
                }
                res.add((E)o);
            }



        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return res;
    }

    private BoundSql parseSql(String sql) {
        if (sql == null || sql == "")
            return null;

        char[] chars = sql.toCharArray();
        BoundSql boundSql = new BoundSql();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '#') {
                i = parseParams(boundSql, i + 1, chars);
                sb.append("?");
                continue;
            }
            sb.append(chars[i]);
        }
        boundSql.setSql(sb.toString());
        return boundSql;
    }

    private int parseParams(BoundSql boundSql, int i, char[] chars) {
        if (chars[i++] != '{')
            throw new RuntimeException("please check your params at " + new String(chars, i - 2, chars.length - i));
        String s = "";
        while (i < chars.length && chars[i] != '}') {
            s = s + chars[i++];
        }
        if (i == chars.length)
            throw new RuntimeException("your sql has missing }");
        if (s == "")
            throw new RuntimeException("please check your params at " + new String(chars, i - 2, chars.length - i));
        boundSql.put(s);
        return i;
    }
}

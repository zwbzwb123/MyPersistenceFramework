package com.zwb.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.zwb.io.XmlResource;
import com.zwb.pojo.Configuration;
import com.zwb.pojo.MappedStatement;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class XmlReader {

    private Configuration configuration;

    public XmlReader() {
        this.configuration = new Configuration();
    }

    public Configuration getConfiguration(InputStream is) {
        try {
            // 将配置文件解析为 Document
            Document document = gerDocument(is);

            // 取得配置文件的根节点
            Element root = document.getRootElement();

            // 解析配置文件配置的数据源
            DataSource ds = parseDataSource(root);

            // 根据配置文件配置的mapper路径，解析mapper
            Map<String, MappedStatement> map = parseMappers(root);

            configuration.setDataSource(ds);
            configuration.setMappedStatements(map);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return configuration;
    }

    private Map<String, MappedStatement> parseMappers(Element root) throws DocumentException {
        Map<String, MappedStatement> map = new HashMap<String, MappedStatement>();
        List<Element> mappers = root.selectNodes("//mapper");
        for (Element mapper : mappers) {
            String path = mapper.attributeValue("path");
            parseMapper(path, map);
        }
        return map;
    }

    private void parseMapper(String path, Map<String, MappedStatement> map) throws DocumentException {
        InputStream resource = XmlResource.getResource(path);
        Document document = gerDocument(resource);
        Element rootElement = document.getRootElement();
        String namespace = rootElement.attributeValue("namespace");
        parseMapper(rootElement, map, namespace);
    }

    private void parseMapper(Element rootElement, Map<String, MappedStatement> map, String namespace) {
        parse(rootElement, "//select", map, namespace);
        parse(rootElement, "//insert", map, namespace);
        parse(rootElement, "//delete", map, namespace);
        parse(rootElement, "//update", map, namespace);
    }

    private void parse(Element rootElement, String s, Map<String, MappedStatement> map, String namespace) {
        List<Element> list = rootElement.selectNodes(s);
        for (Element element : list) {
            MappedStatement statement = new MappedStatement();
            String id = element.attributeValue("id");
            statement.setId(id);
            statement.setSql(element.getTextTrim());
            statement.setParamType(element.attributeValue("paramType"));
            statement.setResultType(element.attributeValue("resultType"));
            map.put(namespace + "." + id, statement);
        }
    }

    private DataSource parseDataSource(Element root) {
        List<Element> elements = root.selectNodes("//property");
        Properties properties = new Properties();
        for (Element element : elements) {
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.setProperty(name, value);
        }

        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(properties.getProperty("driver"));
        ds.setUrl(properties.getProperty("url"));
        ds.setUsername(properties.getProperty("username"));
        ds.setPassword(properties.getProperty("password"));

        return ds;
    }

    private Document gerDocument(InputStream is) throws DocumentException {
        return new SAXReader().read(is);
    }
}

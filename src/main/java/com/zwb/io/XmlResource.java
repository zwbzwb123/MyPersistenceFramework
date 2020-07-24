package com.zwb.io;

import java.io.InputStream;

public class XmlResource {

    public static InputStream getResource(String path) {
        return XmlResource.class.getClassLoader().getResourceAsStream(path);
    }

}

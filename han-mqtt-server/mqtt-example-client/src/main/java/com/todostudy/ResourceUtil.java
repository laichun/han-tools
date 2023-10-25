package com.todostudy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourceUtil {

    private static final String CLASSPATH_PRE = "classpath:";

    public ResourceUtil() {
    }

    public static String getAbsolutePath(String path) {
        return getDecodedPath(getResource(path));
    }

    public static URL getResource(String path) {
        if (StrUtil.startWithIgnoreCase(path, "classpath:")) {
            path = path.substring("classpath:".length());
        }

        return getClassLoader().getResource(path);
    }

    public static InputStream getResourceAsStream(String path) {
        if (StrUtil.startWithIgnoreCase(path, "classpath:")) {
            path = path.substring("classpath:".length());
        }

        return getClassLoader().getResourceAsStream(path);
    }

    public static InputStream getFileResource(String file) {
        try {
            return Files.newInputStream(Paths.get(file));
        } catch (IOException var2) {
            throw new IllegalArgumentException(var2);
        }
    }

    private static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ResourceUtil.class.getClassLoader();
            if (null == classLoader) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
        }

        return classLoader;
    }

    private static String getDecodedPath(URL url) {
        if (null == url) {
            return null;
        } else {
            String path = null;

            try {
                path = url.toURI().getPath();
            } catch (URISyntaxException var3) {
            }

            return null != path ? path : url.getPath();
        }
    }
}

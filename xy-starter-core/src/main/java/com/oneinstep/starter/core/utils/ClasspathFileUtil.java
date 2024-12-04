package com.oneinstep.starter.core.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * 读取classpath 目录下文件的工具类
 **/
@UtilityClass
@Slf4j
public class ClasspathFileUtil {

    /**
     * 从 classpath 路径下读取文件到字符串
     *
     * @param path 文件路径
     * @return 字符串
     */
    public static String readStringFromClasspathFile(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        try (InputStream inputStream = ClasspathFileUtil.class.getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new FileNotFoundException("File not found in classpath: " + path);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
            return sb.toString();
        } catch (IOException e) {
            log.error("readStringFromClasspathFile IOException", e);
        }
        return null;
    }
}


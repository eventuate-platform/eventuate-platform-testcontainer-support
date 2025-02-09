package io.eventuate.testcontainers.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BuildArgsResolver {
    public static Map<String, String> buildArgs() {
        Map<String, String> result = new HashMap<>();

        String classpathBaseImageVersion = getClasspathBaseImageVersion();
        if (classpathBaseImageVersion != null) {
            result.put("baseImageVersion", classpathBaseImageVersion);
        }

        putIfNonNull(result, "baseImageVersion", "eventuate.servicecontainer.baseimage.version");
        putIfNonNull(result, "serviceImageVersion", "eventuate.servicecontainer.serviceimage.version");
        return result;
    }

    private static String getClasspathBaseImageVersion() {
        Properties props = new Properties();
        try {
            props.load(BuildArgsResolver.class.getResourceAsStream("/eventuate.examples.dockerimages.version.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return props.getProperty("version");
    }

    private static void putIfNonNull(Map<String, String> result, String targetProperty, String sourceProperty) {
        String value = System.getProperty(sourceProperty);
        if (value != null) {
            result.put(targetProperty, value);
        }
    }

}

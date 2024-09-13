package io.eventuate.testcontainers.service;

import java.util.HashMap;
import java.util.Map;

public class SystemPropertiesLoader {
    public static Map<String, String> buildArgsFromSystemProperties() {
        Map<String, String> result = new HashMap<>();
        putIfNonNull(result, "baseImageVersion", "eventuate.servicecontainer.baseimage.version");
        putIfNonNull(result, "serviceImageVersion", "eventuate.servicecontainer.serviceimage.version");
        return result;
    }

    private static void putIfNonNull(Map<String, String> result, String targetProperty, String sourceProperty) {
        String value = System.getProperty(sourceProperty);
        if (value != null) {
            result.put(targetProperty, value);
        }
    }

}

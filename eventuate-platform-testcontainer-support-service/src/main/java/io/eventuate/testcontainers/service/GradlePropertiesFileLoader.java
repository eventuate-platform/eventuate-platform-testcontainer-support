package io.eventuate.testcontainers.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class GradlePropertiesFileLoader {
    static Map<String, String> buildArgsFromGradleProperties(String gradlePropertiesPath) {
        Properties gradleProperties = new Properties();

        try (InputStream is = Files.newInputStream(Paths.get(gradlePropertiesPath))) {
            gradleProperties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, String> result = new HashMap<>();
        putIfNonNull(result, "baseImageVersion", gradleProperties, "eventuateExamplesBaseImageVersion");
        putIfNonNull(result, "serviceImageVersion", gradleProperties, "version");
        return result;
    }

    private static void putIfNonNull(Map<String, String> result, String targetProperty, Properties sourceProperties, String sourceProperty) {
        String value = sourceProperties.getProperty(sourceProperty);
        if (value != null) {
            result.put(targetProperty, value);
        }
    }

}

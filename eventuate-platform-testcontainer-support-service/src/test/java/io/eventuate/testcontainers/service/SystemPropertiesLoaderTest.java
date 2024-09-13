package io.eventuate.testcontainers.service;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SystemPropertiesLoaderTest {

    @Test
    public void shouldLoadSystemProperties() {
        Map<String, String> result = SystemPropertiesLoader.buildArgsFromSystemProperties();
        result.forEach((k, v) -> assertNotNull(v));
    }
}
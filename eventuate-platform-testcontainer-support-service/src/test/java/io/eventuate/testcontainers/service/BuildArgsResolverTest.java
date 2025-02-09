package io.eventuate.testcontainers.service;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BuildArgsResolverTest {

    @Test
    public void shouldLoadSystemProperties() {
        Map<String, String> result = BuildArgsResolver.buildArgs();
        result.forEach((k, v) -> assertNotNull(v));
    }
}
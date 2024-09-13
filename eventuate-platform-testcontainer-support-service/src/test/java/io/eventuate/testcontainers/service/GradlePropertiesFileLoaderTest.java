package io.eventuate.testcontainers.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Map;

class GradlePropertiesFileLoaderTest {

    @Test
    public void shouldLoadPropertiesFromGradlePropertiesFile() {
        Map<String, String> result = GradlePropertiesFileLoader.buildArgsFromGradleProperties("src/test/resources/gradle_dot_properties_with_all.properties");
        assertThat(result.size(), equalTo(2));
        result.forEach((k, v) -> assertNotNull(v));
    }

    @Test
    public void shouldLoadPropertiesFromEmptyGradlePropertiesFile() {
        Map<String, String> result = GradlePropertiesFileLoader.buildArgsFromGradleProperties("src/test/resources/gradle_dot_properties_with_none.properties");
        result.forEach((k, v) -> assertNotNull(v));
        assertThat(result.size(), equalTo(1));
    }

}
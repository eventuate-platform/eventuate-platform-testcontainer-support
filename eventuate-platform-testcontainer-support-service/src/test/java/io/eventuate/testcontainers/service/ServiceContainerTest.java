package io.eventuate.testcontainers.service;

import org.junit.jupiter.api.Test;

class ServiceContainerTest {

    @Test
    public void shouldStartContainerBuiltFromDockerfileOnClasspath() {
        try (ServiceContainer sc = ServiceContainer.makeFromDockerfileOnClasspath()) {
            sc.start();
        }
    }

    @Test
    public void shouldStartContainerBuildFromDockerfileInFileSystem() {
        try (ServiceContainer sc = ServiceContainer.makeFromDockerfileInFileSystem("Dockerfile")) {
            sc.start();
        }
    }

}
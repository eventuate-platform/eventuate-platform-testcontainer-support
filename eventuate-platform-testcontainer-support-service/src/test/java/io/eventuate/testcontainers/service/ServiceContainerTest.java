package io.eventuate.testcontainers.service;

import org.junit.jupiter.api.Test;

class ServiceContainerTest {

    @Test
    public void shouldStart() {
        ServiceContainer sc = ServiceContainer.makeFromDockerfileOnClasspath();
        sc.start();
        try {
            // HTTP
            // Messaging
        } finally {
            sc.stop();
        }
    }

}
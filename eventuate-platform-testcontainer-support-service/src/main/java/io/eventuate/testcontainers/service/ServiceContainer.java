package io.eventuate.testcontainers.service;

import io.eventuate.common.testcontainers.EventuateDatabaseContainer;
import io.eventuate.common.testcontainers.EventuateGenericContainer;
import io.eventuate.common.testcontainers.EventuateZookeeperContainer;
import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaContainer;
import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ServiceContainer extends EventuateGenericContainer<ServiceContainer> {


    public ServiceContainer() {
        this("./Dockerfile", "../gradle.properties");
    }

    public ServiceContainer(String dockerFile, String gradlePropertiesPath) {
        this(new ImageFromDockerfile().withDockerfile(FileSystems.getDefault().getPath(dockerFile))
                .withBuildArgs(buildArgsFromGradleProperties(gradlePropertiesPath)));
    }

    public ServiceContainer(ImageFromDockerfile imageFromDockerfile) {
        super(imageFromDockerfile);
        waitingFor(Wait.forHealthcheck());
        // Needed this to avoid missing bean TramSpringCloudSleuthIntegrationCommonConfiguration brave.Tracing
        withEnv("SPRING_SLEUTH_ENABLED", "true");
        withExposedPorts(8080);
    }

    public static ServiceContainer makeFromDockerfileOnClasspath() {
        return makeFromDockerfileOnClasspath(".", "Dockerfile");
    }

    @NotNull
    private static ServiceContainer makeFromDockerfileOnClasspath(String buildContextPath, String dockerfileName) {
        String relativeToDockerfile = classpathResourceToRelativePath(buildContextPath, dockerfileName);
        return new ServiceContainer(new ImageFromDockerfile()
                .withFileFromPath(buildContextPath, FileSystems.getDefault().getPath("."))
                .withDockerfilePath(relativeToDockerfile)
                .withBuildArgs(buildArgsFromSystemProperties()));
    }

    @NotNull
    private static String classpathResourceToRelativePath(String buildContextPath, String dockerfileName) {
        Path path = Paths.get(ServiceContainer.class.getClassLoader().getResource(dockerfileName).getFile()).toAbsolutePath();
        Path root = Paths.get(buildContextPath).toAbsolutePath();
        return root.relativize(path).toString();
    }

    private static Map<String, String> buildArgsFromSystemProperties() {
        Map<String, String> result = new HashMap<>();
        result.put("baseImageVersion", System.getProperty("eventuate.servicecontainer.baseimage.version"));
        if (System.getProperty("eventuate.servicecontainer.serviceimage.version") != null)
            result.put("serviceImageVersion", System.getProperty("eventuate.servicecontainer.serviceimage.version"));
        return result;
    }

    @Override
    protected int getPort() {
        return 8080;
    }

    private static Map<String, String> buildArgsFromGradleProperties(String gradlePropertiesPath) {
        Properties gradleProperties = new Properties();

        try (InputStream is = Files.newInputStream(Paths.get(gradlePropertiesPath))) {
            gradleProperties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, String> result = new HashMap<>();
        result.put("baseImageVersion", gradleProperties.getProperty("eventuateExamplesBaseImageVersion"));
        result.put("serviceImageVersion", gradleProperties.getProperty("version"));
        return result;
    }

    public ServiceContainer withZookeeper(EventuateZookeeperContainer zookeeper) {
        return this.withEnv("EVENTUATELOCAL_ZOOKEEPER_CONNECTION_STRING", zookeeper.getConnectionString());
    }

    public ServiceContainer withKafka(EventuateKafkaContainer kafka) {
        return this.withEnv("EVENTUATELOCAL_KAFKA_BOOTSTRAP_SERVERS", kafka.getConnectionString());
    }

    public ServiceContainer withDatabase(EventuateDatabaseContainer<?> database) {
        withEnv("SPRING_DATASOURCE_URL", database.getJdbcUrl());
        withEnv("SPRING_DATASOURCE_USERNAME", database.getCredentials().userName);
        withEnv("SPRING_DATASOURCE_PASSWORD", database.getCredentials().password);
        withEnv("SPRING_DATASOURCE_DRIVER_CLASS_NAME", database.getDriverClassName());
        withEnv("EVENTUATE_DATABASE_SCHEMA", database.getEventuateDatabaseSchema());
        return this;
    }


}

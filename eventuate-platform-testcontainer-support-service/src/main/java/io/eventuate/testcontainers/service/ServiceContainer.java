package io.eventuate.testcontainers.service;

import io.eventuate.common.testcontainers.EventuateDatabaseContainer;
import io.eventuate.common.testcontainers.EventuateGenericContainer;
import io.eventuate.common.testcontainers.EventuateZookeeperContainer;
import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaContainer;
import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaNativeContainer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ServiceContainer extends EventuateGenericContainer<ServiceContainer> {

    private static Logger logger = LoggerFactory.getLogger(ServiceContainer.class);

    public ServiceContainer() {
        this("./Dockerfile", "../gradle.properties");
    }

    public ServiceContainer(String dockerFile, String gradlePropertiesPath) {
        this(new ImageFromDockerfile().withDockerfile(FileSystems.getDefault().getPath(dockerFile))
                .withBuildArgs(GradlePropertiesFileLoader.buildArgsFromGradleProperties(gradlePropertiesPath)));
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
        logger.info("Using buildContextPath {} Dockerfile {}", buildContextPath, relativeToDockerfile);
        return new ServiceContainer(new ImageFromDockerfile()
                .withFileFromPath(buildContextPath, FileSystems.getDefault().getPath("."))
                .withDockerfilePath(relativeToDockerfile)
                .withBuildArgs(SystemPropertiesLoader.buildArgsFromSystemProperties()));
    }

    public static ServiceContainer makeFromDockerfileInFileSystem(String dockerFile) {
        Path absolutePathToDockerfile = FileSystems.getDefault().getPath(dockerFile).toAbsolutePath();
        logger.info("Using Dockerfile {}", absolutePathToDockerfile);
        return new ServiceContainer(new ImageFromDockerfile()
                .withDockerfile(absolutePathToDockerfile)
                .withBuildArgs(SystemPropertiesLoader.buildArgsFromSystemProperties()));
    }

    @NotNull
    private static String classpathResourceToRelativePath(String buildContextPath, String dockerfileName) {
        Path path = Paths.get(ServiceContainer.class.getClassLoader().getResource(dockerfileName).getFile()).toAbsolutePath();
        Path root = Paths.get(buildContextPath).toAbsolutePath();
        return root.relativize(path).toString();
    }

    @Override
    protected int getPort() {
        return 8080;
    }

    public ServiceContainer withZookeeper(EventuateZookeeperContainer zookeeper) {
        dependsOn(zookeeper);
        return this.withEnv("EVENTUATELOCAL_ZOOKEEPER_CONNECTION_STRING", zookeeper.getConnectionString());
    }

    public ServiceContainer withKafka(EventuateKafkaContainer kafka) {
        dependsOn(kafka);
        return this.withEnv("EVENTUATELOCAL_KAFKA_BOOTSTRAP_SERVERS", kafka.getConnectionString());
    }

    public ServiceContainer withKafka(EventuateKafkaNativeContainer kafka) {
        dependsOn(kafka);
        return this.withEnv("EVENTUATELOCAL_KAFKA_BOOTSTRAP_SERVERS", kafka.getBootstrapServersForContainer());
    }

    public ServiceContainer withDatabase(EventuateDatabaseContainer<?> database) {
        dependsOn(database);
        withEnv("SPRING_DATASOURCE_URL", database.getJdbcUrl());
        withEnv("SPRING_DATASOURCE_USERNAME", database.getCredentials().userName);
        withEnv("SPRING_DATASOURCE_PASSWORD", database.getCredentials().password);
        withEnv("SPRING_DATASOURCE_DRIVER_CLASS_NAME", database.getDriverClassName());
        withEnv("EVENTUATE_DATABASE_SCHEMA", database.getEventuateDatabaseSchema());
        return this;
    }


}

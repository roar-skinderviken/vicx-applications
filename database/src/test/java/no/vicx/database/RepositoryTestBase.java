package no.vicx.database;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@DataJpaTest
public class RepositoryTestBase {
    private static final String POSTGRES_DOCKER_IMAGE_NAME = "postgres:17-alpine";

    @SuppressWarnings("unused")
    @ServiceConnection
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(
            DockerImageName.parse(POSTGRES_DOCKER_IMAGE_NAME)
                    .asCompatibleSubstituteFor("postgres")
    )
            .withEnv("TZ", "Europe/Oslo")
            .waitingFor(Wait.forListeningPort());

    @Autowired
    protected EntityManager entityManager;
}

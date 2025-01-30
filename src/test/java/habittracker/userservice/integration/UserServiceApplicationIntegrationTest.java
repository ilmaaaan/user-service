package habittracker.userservice.integration;

import habittracker.userservice.UserServiceApplication;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("dev")
@Testcontainers
class UserServiceApplicationIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> userServiceDbContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("user_service_testdb")
            .withUsername("postgres")
            .withPassword("password");

    @Container
    public static PostgreSQLContainer<?> appMetricsDbContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("metrics_service_testdb")
            .withUsername("metrics")
            .withPassword("metrics");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        // Настройка для основной базы данных
        registry.add("spring.datasource.user-service-db.url", userServiceDbContainer::getJdbcUrl);
        registry.add("spring.datasource.user-service-db.username", userServiceDbContainer::getUsername);
        registry.add("spring.datasource.user-service-db.password", userServiceDbContainer::getPassword);

        // Настройка для базы данных метрик
        registry.add("spring.datasource.app-metrics-db.url", appMetricsDbContainer::getJdbcUrl);
        registry.add("spring.datasource.app-metrics-db.username", appMetricsDbContainer::getUsername);
        registry.add("spring.datasource.app-metrics-db.password", appMetricsDbContainer::getPassword);
    }

    @BeforeAll
    static void loadEnv() {
        String dotenvPath = new File(System.getProperty("user.dir")).getParent();

        Dotenv dotenv = Dotenv.configure()
                .directory(dotenvPath)
                .filename(".env.dev")
                .load();

        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    @Test
    void contextLoads() {
        // Проверка того, что контекст загружается успешно
    }
}

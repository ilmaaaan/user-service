package habittracker.userservice.integration;

import habittracker.userservice.UserServiceApplication;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = UserServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Testcontainers
public class ActuatorConfigIntegrationTest {

    // пред-установка для тестов
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

    @BeforeAll // подгружаем переменную среду перед каждым тестом
    static void loadEnv() {
        String dotenvPath = new File(System.getProperty("user.dir")).getParent();

        Dotenv dotenv = Dotenv.configure()
                .directory(dotenvPath)
                .filename(".env.dev")
                .load();

        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    // тесты
    @Autowired
    private DataSourceProperties dataSourceProperties;
    @Autowired
    private DataSource actuatorInfoDataSource;

    // интеграционный тест актуатора на то, что все properties базы данных подгрузились правильно
    @Test
    @DisplayName("Integration ActuatorConfig Test: actuatorInfoDataSourcePropertiesTest()")
    void actuatorInfoDataSourcePropertiesTest() {
        // проверка настроек DataSourceProperties
        assertNotNull(dataSourceProperties, "DataSourceProperties должно быть настроено");
        assertThat(dataSourceProperties.getUrl()).isNotEmpty();
        assertThat(dataSourceProperties.getUsername()).isNotEmpty();
        assertThat(dataSourceProperties.getPassword()).isNotEmpty();
    }

    // интеграционный тест актуатора на то, правильно ли инициализировался DataSource для работы TransactionManager
    @Test
    @DisplayName("Integration ActuatorConfig Test: transactionManagerTest()")
    void transactionManagerTest() {
        // проверка DataSource (его корректность также важна для работы TransactionManager)
        assertNotNull(actuatorInfoDataSource, "DataSource должен быть настроен");
        assertThat(actuatorInfoDataSource).isNotNull();
    }
}

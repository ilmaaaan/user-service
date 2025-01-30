package habittracker.userservice.integration;

import habittracker.userservice.UserServiceApplication;
import habittracker.userservice.actuator.controller.ActuatorController;
import habittracker.userservice.actuator.dto.ServiceInfoResponse;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = UserServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@Testcontainers
public class ActuatorControllerIntegrationTest {

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
    private ActuatorController controller;

    @BeforeEach
    public void setUp() {
        controller = new ActuatorController();
    }

    // интеграционное тестирование метода .getServiceInfo ActuatorController-а с валидными данными
    @Test
    @DisplayName("Integration Actuator Test: .getServiceInfo valid test")
    void getServiceInfoTest() {
        try {
            Field actuatorUrlField = ActuatorController.class.getDeclaredField("actuatorUrl");
            actuatorUrlField.setAccessible(true);
            actuatorUrlField.set(controller, "https://example.com/actuator/info/service-info");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set actuatorUrl", e);
        }

        try {
            Field restTemplateField = ActuatorController.class.getDeclaredField("restTemplate");
            restTemplateField.setAccessible(true);
            RestTemplate mockRestTemplate = mock(RestTemplate.class);
            when(mockRestTemplate.getForEntity(anyString(), eq(ServiceInfoResponse.class))).thenReturn(
                    new ResponseEntity<>(new ServiceInfoResponse(), HttpStatus.OK));
            restTemplateField.set(controller, mockRestTemplate);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set restTemplate", e);
        }

        ServiceInfoResponse result = controller.getServiceInfo();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.OK, new ResponseEntity<>(new ServiceInfoResponse(), HttpStatus.OK)
                .getStatusCode());
        Assertions.assertNotEquals(null, result);
    }

    // интеграционное тестирование метода .getServiceInfo без указания uri
    @Test
    @DisplayName("Integration ActuatorController Test: .getServiceInfo with missing uri")
    void getServiceInfoWithMissingUrlTest() {
        controller = new ActuatorController();
        try {
            controller.getServiceInfo();
            Assertions.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("URI is not absolute", e.getMessage());
        }
    }
}

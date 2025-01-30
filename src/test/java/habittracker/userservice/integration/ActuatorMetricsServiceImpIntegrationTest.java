package habittracker.userservice.integration;

import habittracker.userservice.UserServiceApplication;
import habittracker.userservice.actuator.controller.ActuatorController;
import habittracker.userservice.actuator.dto.ServiceInfoResponse;
import habittracker.userservice.actuator.entity.Metrics;
import habittracker.userservice.actuator.repository.MetricsRepository;
import habittracker.userservice.actuator.service.MetricsServiceImp;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = UserServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Testcontainers
public class ActuatorMetricsServiceImpIntegrationTest {

    // подготовка контейнеров
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

    // тесты и зависимости
    @InjectMocks
    private MetricsServiceImp metricsService;

    @Mock
    private MetricsRepository metricsRepository;

    @Mock
    private ActuatorController actuatorController;

    @Value("${delete.before.seconds}")
    private Long seconds;

    @Test
    @DisplayName("Integration MetricsServiceImpl Test: .saveInfo()")
    void saveInfoSuccessTest() {

        Metrics mockMetrics = new Metrics();
        mockMetrics.setId(1L);

        ServiceInfoResponse mockServiceInfoResponse = new ServiceInfoResponse();
        mockServiceInfoResponse.setMetrics(mockMetrics);

        when(actuatorController.getServiceInfo()).thenReturn(mockServiceInfoResponse);

        metricsService.saveInfo();

        verify(metricsRepository, times(1)).save(mockMetrics);
    }

    @Test
    @DisplayName("Integration MetricsServiceImpl Test: .saveInfo() with no metrics")
    void saveInfoNoMetricsTest() {

        ServiceInfoResponse mockServiceInfoResponse = new ServiceInfoResponse();

        mockServiceInfoResponse.setMetrics(null);

        when(actuatorController.getServiceInfo()).thenReturn(mockServiceInfoResponse);

        metricsService.saveInfo();

        verify(metricsRepository, never()).save(any());
    }

    @Test
    @DisplayName("Integration MetricsServiceImpl Test: .saveInfo() w multiple success")
    void saveInfoMultipleSuccessTest() {
        Metrics mockMetrics = new Metrics();
        ServiceInfoResponse mockServiceInfoResponse = new ServiceInfoResponse();
        mockServiceInfoResponse.setMetrics(mockMetrics);

        when(actuatorController.getServiceInfo()).thenReturn(mockServiceInfoResponse);

        for (int i = 0; i < 5; i++) {
            metricsService.saveInfo();
        }

        verify(metricsRepository, times(5)).save(mockMetrics);
    }

    @Test
    @DisplayName("Integration MetricsServiceImpl Test: .testRepository()")
    void testRepositoryTest() {
        seconds = 60L;
        LocalDateTime timestamp = LocalDateTime.now().minusSeconds(seconds);

        Metrics metric1 = new Metrics();
        metric1.setCreatedAt(LocalDateTime.now().minusMinutes(2));

        Metrics metric2 = new Metrics();
        metric2.setCreatedAt(LocalDateTime.now().minusMinutes(3));

        when(metricsRepository.findAllWithCreatedAtBefore(timestamp)).thenReturn(Arrays.asList(metric1, metric2));

        List<Metrics> metrics = metricsRepository.findAllWithCreatedAtBefore(timestamp);

        Assertions.assertEquals(2, metrics.size());
    }

    @Test
    @DisplayName("Integration MetricsServiceImpl Test: .getInfo() no metrics")
    void getInfoNoMetricsTest() {
        seconds = 60L;
        LocalDateTime timestamp = LocalDateTime.now().minusSeconds(seconds);

        when(metricsRepository.findAllWithCreatedAtBefore(timestamp)).thenReturn(Collections.emptyList());

        List<Metrics> result = metricsService.getInfo(seconds);

        Assertions.assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Integration MetricsServiceImpl Test: .getInfo() with 0 seconds")
    void getInfoZeroSecondsTest() {
        seconds = 0L;
        LocalDateTime timestamp = LocalDateTime.now().minusSeconds(seconds);

        when(metricsRepository.findAllWithCreatedAtBefore(timestamp)).thenReturn(Collections.emptyList());

        List<Metrics> result = metricsService.getInfo(seconds);

        Assertions.assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Integration MetricsServiceImpl Test: .testRepository() w exception")
    void testRepositoryWithExceptionTest() {
        LocalDateTime timestamp = LocalDateTime.now().minusSeconds(60L);
        when(metricsRepository.findAllWithCreatedAtBefore(timestamp)).thenThrow(new RuntimeException("Database error"));

        Exception exception = Assertions.assertThrows(RuntimeException.class, () ->
                metricsRepository.findAllWithCreatedAtBefore(timestamp));

        Assertions.assertEquals("Database error", exception.getMessage());
    }

    @Test
    @DisplayName("Integration MetricsServiceImpl Test: .deleteInfo() w metrics")
    void deleteInfoWithMetricsTest() {
        LocalDateTime now = LocalDateTime.now();

        Metrics metric1 = new Metrics();

        metric1.setCreatedAt(now.minusSeconds(30));

        Metrics metric2 = new Metrics();

        metric2.setCreatedAt(now.minusSeconds(20));

        List<Metrics> metricsList = Arrays.asList(metric1, metric2);

        long secondsValue = 60;

        ReflectionTestUtils.setField(metricsService, "seconds", secondsValue);

        when(metricsRepository.findAllWithCreatedAtBefore(any())).thenReturn(metricsList);

        metricsService.deleteInfo();

        verify(metricsRepository).deleteAll(metricsList);
    }

    @Test
    @DisplayName("Integration MetricsServiceImpl Test: .getInfo() w no metrics")
    void deleteInfoNoMetricsTest() {
        when(metricsService.getInfo(seconds)).thenReturn(Collections.emptyList());

        long secondsValue = 60;
        ReflectionTestUtils.setField(metricsService, "seconds", secondsValue);

        metricsService.deleteInfo();

        verify(metricsRepository).findAllWithCreatedAtBefore(any());
        verify(metricsRepository).deleteAll(Collections.emptyList());
        verifyNoMoreInteractions(metricsRepository);
    }

    @Test
    @DisplayName("Integration MetricsServiceImpl Test: .testDelete() before seconds property")
    void testDeleteBeforeSecondsPropertyTest() {
        Assertions.assertNotNull(seconds);
        Assertions.assertTrue(seconds > 0);
    }
}

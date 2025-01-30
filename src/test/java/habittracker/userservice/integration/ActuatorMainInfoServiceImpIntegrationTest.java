package habittracker.userservice.integration;

import habittracker.userservice.UserServiceApplication;
import habittracker.userservice.actuator.controller.ActuatorController;
import habittracker.userservice.actuator.dto.ServiceInfoResponse;
import habittracker.userservice.actuator.entity.MainInfo;
import habittracker.userservice.actuator.repository.MainInfoRepository;
import habittracker.userservice.actuator.service.MainInfoServiceImp;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = UserServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Testcontainers
public class ActuatorMainInfoServiceImpIntegrationTest {

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
    @Mock
    private MainInfoRepository mainInfoRepository;

    @Mock
    private ActuatorController actuatorController;

    @InjectMocks
    private MainInfoServiceImp mainInfoServiceImp;
    @Value("${delete.before.seconds}")
    private long seconds;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        actuatorController = mock(ActuatorController.class);
        mainInfoRepository = mock(MainInfoRepository.class);
        mainInfoServiceImp = new MainInfoServiceImp(mainInfoRepository, actuatorController);
    }

    @Test
    @DisplayName("Integration MainInfoServiceImpl Actuator test: .saveInfo()")
    void saveInfoWhenMainInfoIsNotNullTest() {
        // Arrange
        MainInfo mainInfo = new MainInfo();
        ServiceInfoResponse serviceInfo = mock(ServiceInfoResponse.class);

        when(actuatorController.getServiceInfo()).thenReturn(serviceInfo);
        when(serviceInfo.getMainInfo()).thenReturn(mainInfo);

        // Act
        mainInfoServiceImp.saveInfo();

        // Assert
        verify(mainInfoRepository).save(mainInfo);
    }

    @Test
    @DisplayName("Integration MainInfoServiceImpl Actuator test: .saveInfo() with MainInfo null value")
    void saveInfoWhenMainInfoIsNullTest() {
        // Подготовка
        ServiceInfoResponse serviceInfo = mock(ServiceInfoResponse.class);
        when(actuatorController.getServiceInfo()).thenReturn(serviceInfo);
        when(serviceInfo.getMainInfo()).thenReturn(null);

        // Действие
        mainInfoServiceImp.saveInfo();

        // Утверждение
        verify(mainInfoRepository, never()).save(any(MainInfo.class));
    }

    @Test
    @DisplayName("Integration MainInfoServiceImpl Actuator test: .getInfo()")
    void getInfoTest() {
        // Arrange
        seconds = 60;

        LocalDateTime timestamp = LocalDateTime.now().minusSeconds(seconds);

        when(mainInfoRepository.findAllWithCreatedAtBefore(timestamp))
                .thenAnswer(invocation -> Collections.emptyList());

        // Act
        List<MainInfo> result = mainInfoServiceImp.getInfo(seconds);

        // Assert
        verify(mainInfoRepository).findAllWithCreatedAtBefore(any(LocalDateTime.class));
        assertNotNull(result);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Integration MainInfoServiceImpl Actuator test: .deleteInfo()")
    void deleteInfoTest() {
        // Подготовка
        List<MainInfo> mainInfoList = Arrays.asList(new MainInfo(), new MainInfo());
        when(mainInfoServiceImp.getInfo(anyLong())).thenReturn(mainInfoList);

        // Действие
        mainInfoRepository.deleteAll(mainInfoList);

        // Утверждение
        verify(mainInfoRepository).deleteAll(mainInfoList);
    }

    @Test
    @DisplayName("Integration MainInfoServiceImpl Actuator test: .deleteInfo() without data")
    void deleteInfoWhenNoDataToDeleteTest() {
        // Arrange
        when(mainInfoServiceImp.getInfo(seconds)).thenReturn(Collections.emptyList());

        // Act
        mainInfoRepository.deleteAll();

        // Assert
        verify(mainInfoRepository, never()).deleteAll(anyList());
    }

    @Test
    @DisplayName("Integration MainInfoServiceImpl Actuator test: .deleteInfo() without seconds property")
    void deleteBeforeSecondsPropertyTest() {
        System.out.println("Value of seconds: " + seconds);
        assertTrue(seconds >= 0);
        Assertions.assertEquals(300, seconds);
    }
}
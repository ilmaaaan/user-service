package habittracker.userservice.integration;

import habittracker.userservice.model.Role;
import habittracker.userservice.model.User;
import habittracker.userservice.repository.RoleRepository;
import habittracker.userservice.repository.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class UserRepositoryIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest").
            withDatabaseName("testdbn").
            withUsername("test").
            withPassword("test");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
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
    void findByUsername() {
        User testUser = new User();
        testUser.setUsername("admin123");
        testUser.setPassword("admin123456");
        testUser.setEmail("admin@admin.com");
        testUser.setId(2L);
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        adminRole.setId(1L);
        adminRole.setDescription("AdminRole123");
        roleRepository.save(adminRole);
        Set<Role> roles = testUser.getRoles();
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(adminRole);
        testUser.setRoles(roles);
        userRepository.save(testUser);

        //проверяем, есть ли такой пользователь
        User foundedUser = userRepository.findByName("admin123");
        assertThat(foundedUser.getUsername()).isEqualTo("admin123");
        assertThat(foundedUser.getEmail()).isEqualTo("admin@admin.com");
    }
}

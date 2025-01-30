package habittracker.userservice.integration;

import habittracker.userservice.model.Role;
import habittracker.userservice.model.User;
import habittracker.userservice.repository.RoleRepository;
import habittracker.userservice.repository.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class UserPostgresIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"))
            .withDatabaseName("test-db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeAll
    static void loadEnv() {
        String dotenvPath = new File(System.getProperty("user.dir")).getParent();

        Dotenv dotenv = Dotenv.configure()
                .directory(dotenvPath)
                .filename(".env.dev")
                .load();

        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
        entityManager.createNativeQuery("ALTER SEQUENCE users_id_seq RESTART WITH 1").executeUpdate();
    }

    @Test
    void testEmptyGetAllUsers() {
        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    void testNotEmptyGetAllUsers() {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("password1");
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("password2");
        user2.setEmail("user2@example.com");

        userRepository.save(user1);
        userRepository.save(user2);

        assertThat(userRepository.findAll()).hasSize(2);
    }

    @Test
    void testGetUserByIdNotFound() {
        Optional<User> user = userRepository.findById(999L);
        assertThat(user).isEmpty();
    }

    @Test
    void testDeleteRoleAssignedToUserManually() {
        Role role = new Role();
        role.setName("ROLE_USER");
        roleRepository.save(role);

        User user = new User();
        user.setUsername("user");
        user.setPassword("password123");
        user.setEmail("user@example.com");
        user.getRoles().add(role);
        userRepository.save(user);

        user.getRoles().remove(role);
        userRepository.save(user);
        roleRepository.deleteById(role.getId());

        Optional<User> foundUser = userRepository.findById(user.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getRoles()).isEmpty();
    }

    @Test
    void testGetUserById() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setEmail("user@example.com");
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(user.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("user");
    }

    @Test
    void testCreateUser() {
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("password123");
        user.setEmail("newuser@example.com");

        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isPositive();
        assertThat(savedUser.getUsername()).isEqualTo("newuser");
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setEmail("user@example.com");
        userRepository.save(user);

        userRepository.deleteById(user.getId());

        Optional<User> deletedUser = userRepository.findById(user.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setEmail("user@example.com");
        userRepository.save(user);
        user.setUsername("updatedUser");
        user.setEmail("updateduser@example.com");
        User updatedUser = userRepository.save(user);

        assertThat(updatedUser.getUsername()).isEqualTo("updatedUser");
        assertThat(updatedUser.getEmail()).isEqualTo("updateduser@example.com");
    }

    @Test
    void testAssignRoleToUser() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setEmail("user@example.com");
        userRepository.save(user);

        Role role = new Role();
        role.setName("ROLE_USER");
        roleRepository.save(role);

        user.getRoles().add(role);
        User updatedUser = userRepository.save(user);

        assertThat(updatedUser.getRoles()).hasSize(1);
        assertThat(updatedUser.getRoles().iterator().next().getName()).isEqualTo("ROLE_USER");
    }
}

package habittracker.userservice.unit.test.service;

import habittracker.userservice.audit.UserActionLog;
import habittracker.userservice.model.Role;
import habittracker.userservice.model.User;
import habittracker.userservice.repository.RoleRepository;
import habittracker.userservice.repository.UserActionLogRepository;
import habittracker.userservice.repository.UserRepository;
import habittracker.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.s3.S3Client;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
class UserServiceUnitTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private S3Client s3Client;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserActionLogRepository userActionLogRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private User testUser2;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setId(111L);

        Role existingRole = new Role();
        existingRole.setName("ROLE_USER");

        Set<Role> roles = new HashSet<>();
        roles.add(existingRole);

        testUser.setRoles(roles);

        testUser2 = new User();
        testUser2.setUsername("testUser2");
        testUser2.setEmail("test2@example.com");
        testUser2.setPassword("password2");
        testUser2.setId(222L);

        Role anotherRole = new Role();
        anotherRole.setName("ROLE_ADMIN");

        Set<Role> roles2 = new HashSet<>();
        roles2.add(anotherRole);

        testUser2.setRoles(roles2);
    }

    @Test
    void loadUserByUsername() {
        when(userRepository.findByName(testUser.getUsername())).thenReturn(testUser);

        UserDetails userDetails = userService.loadUserByUsername(testUser.getUsername());

        assertEquals(testUser.getEmail(), userDetails.getUsername());
        assertEquals(testUser.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        verify(userRepository, times(1)).findByName(testUser.getUsername());
    }

    @Test
    void loadUserByUsernameUserNotFound() {
        when(userRepository.findByName(testUser.getUsername())).thenReturn(null);

        String testUserStr = testUser.getUsername();
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(testUserStr);
        });
        verify(userRepository, times(1)).findByName(testUser.getUsername());
    }

    @Test
    void testCreateUser() {
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        userService.addUser("testUser", "password123", "test@test.com");

        verify(passwordEncoder).encode("password123");

        verify(userRepository).save(any(User.class));

        verify(userActionLogRepository).save(any(UserActionLog.class));
    }

    @Test
    void getUserByUserByName() {
        when(userRepository.findByName(testUser.getUsername())).thenReturn(testUser);

        User foundUser = userService.getUserByName(testUser.getUsername());

        assertEquals(testUser, foundUser);
        verify(userRepository, times(1)).findByName(testUser.getUsername());
    }

    @Test
    void updateUser() {
        String name = "name";
        String address = "address";

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        userService.updateUser(name, address, testUser.getId());

        verify(userRepository, times(1)).save(testUser);
        assertEquals(name, testUser.getName());
        assertEquals(address, testUser.getAddress());
    }

    @Test
    void updateUserUserNotFound() {
        String name = "name";
        String address = "address";
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.updateUser(name, address, userId)
        );

        assertEquals("User not found", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void banUser() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        userService.banUser(testUser.getId());

        verify(userRepository, times(1)).save(testUser);
        assertFalse(testUser.isAccountNonLocked());
    }

    @Test
    void banUserUserNotFound() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

        Long userId = testUser.getId();
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.banUser(userId);
        });

        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void unbanUser() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        userService.unbanUser(testUser.getId());

        verify(userRepository, times(1)).save(testUser);
        assertTrue(testUser.isAccountNonLocked());
    }

    @Test
    void unbanUserUserNotFound() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

        Long userId = testUser.getId();
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.unbanUser(userId);
        });

        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void getUserById() {
        when(userRepository.findByIdWithRoles(testUser.getId())).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(testUser.getId());

        assertNotNull(result);
        assertEquals(testUser, result);
        assertEquals("testUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("password", result.getPassword());

        assertNotNull(result.getRoles());
        assertTrue(result.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_USER")));

        verify(userRepository, times(1)).findByIdWithRoles(testUser.getId());
    }

    @Test
    void getUserByIdUserNotFound() {
        when(userRepository.findByIdWithRoles(testUser.getId())).thenReturn(Optional.empty());

        Long userId = testUser.getId();
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.getUserById(userId)
        );

        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findByIdWithRoles(testUser.getId());
    }

    @Test
    void getAllUsers() {
        List<User> mockUsers = Arrays.asList(testUser, testUser2);

        when(userRepository.findAll()).thenReturn(mockUsers);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(testUser, result.get(0));
        assertEquals(testUser2, result.get(1));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteUser() {
        doNothing().when(userRepository).deleteById(testUser.getId());

        userService.deleteUser(testUser.getId());

        verify(userRepository, times(1)).deleteById(testUser.getId());
    }

    @Test
    void assignRoleToUser() {
        Role newRole = new Role();
        newRole.setName("ROLE_TEST");

        when(userRepository.findByEmailWithRoles(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName(newRole.getName())).thenReturn(null);
        when(roleRepository.save(any(Role.class))).thenReturn(newRole);

        userService.assignRoleToUser(testUser.getEmail(), newRole);

        verify(roleRepository, times(1)).save(newRole); // Проверяем, что новая роль сохранена
        verify(userRepository, times(1)).save(testUser); // Проверяем, что пользователь обновлен

        assertEquals(2, testUser.getRoles().size());
        assertTrue(testUser.getRoles().contains(newRole));
    }

    @Test
    void assignRoleToUserUserNotFound() {
        Role newRole = new Role();
        newRole.setName("ROLE_TEST");

        when(userRepository.findByEmailWithRoles(testUser.getEmail())).thenReturn(Optional.empty());

        String email = testUser.getEmail();
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.assignRoleToUser(email, newRole);
        });
    }
}
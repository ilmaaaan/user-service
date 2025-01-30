package habittracker.userservice.unit.test.controller;

import habittracker.userservice.controller.UserController;
import habittracker.userservice.model.User;
import habittracker.userservice.service.UserActionLogService;
import habittracker.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.security.Principal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
class UserControllerUnitTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserActionLogService userActionLogService;

    @Mock
    private Principal principal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadAvatar() throws IOException {
        String username = "testUser";
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setId(1L);
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "test content".getBytes());
        when(principal.getName()).thenReturn(username);
        when(userService.getUserByName(username)).thenReturn(testUser);

        ResponseEntity<String> response = userController.uploadAvatar(username, file, principal);

        assertEquals("avatar uploaded", response.getBody());
        verify(userService).uploadAvatar(username, file);
        verify(userActionLogService).logAction(1L, "avatar uploaded successfully");
    }

    @Test
    void updateProfile() {
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setId(1L);
        String name = "newName";
        String address = "newAddress";
        String username = "testUser";
        when(principal.getName()).thenReturn(username);
        when(userService.getUserByName(username)).thenReturn(testUser);

        ResponseEntity<String> response = userController.updateProfile(name, address, principal);

        assertEquals("User updated successfully", response.getBody());
        verify(userService).updateUser(name, address, 1L);
        verify(userActionLogService).logAction(1L, "profile updated successfully");
    }
}


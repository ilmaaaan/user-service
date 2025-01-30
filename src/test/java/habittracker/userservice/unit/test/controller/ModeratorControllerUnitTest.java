package habittracker.userservice.unit.test.controller;

import habittracker.userservice.controller.ModeratorController;
import habittracker.userservice.model.User;
import habittracker.userservice.service.UserActionLogService;
import habittracker.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ModeratorControllerUnitTest {
    @Mock
    private UserService userService;

    @Mock
    private UserActionLogService userActionLogService;

    @InjectMocks
    private ModeratorController moderatorController;

    private Principal principal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        principal = () -> "moderator";
    }

    @Test
    void getAllUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("testUser");
        user1.setPassword("password1");
        user1.setEmail("test@example.com");
        user1.setName("User1");
        user1.setAddress("Address1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("testUser2");
        user2.setPassword("password2");
        user2.setEmail("test2@example.com");
        user2.setName("User2");
        user2.setAddress("Address2");

        List<User> users = List.of(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        List<User> response = moderatorController.getAllUsers();

        assertEquals(2, response.size());

        User responseUser1 = response.get(0);
        assertEquals(Long.valueOf(1L), responseUser1.getId());
        assertEquals("testUser", responseUser1.getUsername());
        assertEquals("test@example.com", responseUser1.getEmail());
        assertEquals("User1", responseUser1.getName());
        assertEquals("Address1", responseUser1.getAddress());

        User responseUser2 = response.get(1);
        assertEquals(Long.valueOf(2L), responseUser2.getId());
        assertEquals("testUser2", responseUser2.getUsername());
        assertEquals("test2@example.com", responseUser2.getEmail());
        assertEquals("User2", responseUser2.getName());
        assertEquals("Address2", responseUser2.getAddress());

        verify(userService).getAllUsers();

        when(userService.getAllUsers()).thenReturn(null);

        List<User> nullResponse = moderatorController.getAllUsers();

        assertNotNull(nullResponse);
        assertTrue(nullResponse.isEmpty());
    }

    @Test
    void blockUser() {
        long userIdToBlock = 42L;
        long moderatorId = 1L;

        User moderator = new User();
        moderator.setId(moderatorId);

        when(userService.getUserByName("moderator")).thenReturn(moderator);
        doNothing().when(userService).banUser(userIdToBlock);
        doNothing().when(userActionLogService)
                .logAction(moderatorId, "User " + userIdToBlock + " blocked");

        ResponseEntity<String> response = moderatorController.blockUser(userIdToBlock, principal);

        assertEquals(ResponseEntity.ok("User blocked"), response);
        verify(userService).banUser(userIdToBlock);
        verify(userActionLogService).logAction(moderatorId, "User " + userIdToBlock + " blocked");
    }

    @Test
    void unblockUser() {
        long userIdToUnblock = 42L;
        long moderatorId = 1L;

        User moderator = new User();
        moderator.setId(moderatorId);

        when(userService.getUserByName("moderator")).thenReturn(moderator);
        doNothing().when(userService).unbanUser(userIdToUnblock);
        doNothing().when(userActionLogService)
                .logAction(moderatorId, "User " + userIdToUnblock + " blocked");

        ResponseEntity<String> response = moderatorController.unblockUser(userIdToUnblock, principal);

        assertEquals(ResponseEntity.ok("User blocked"), response);
        verify(userService).unbanUser(userIdToUnblock);
        verify(userActionLogService).logAction(moderatorId, "User " + userIdToUnblock + " blocked");
    }

}

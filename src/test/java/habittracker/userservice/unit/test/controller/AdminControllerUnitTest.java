package habittracker.userservice.unit.test.controller;


import habittracker.userservice.audit.UserActionLog;
import habittracker.userservice.controller.AdminController;
import habittracker.userservice.model.Role;
import habittracker.userservice.model.User;
import habittracker.userservice.service.UserActionLogService;
import habittracker.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminControllerUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private UserActionLogService userActionLogService;

    @InjectMocks
    private AdminController adminController;

    private Principal principal;

    @BeforeEach
    void setUp() {
        principal = () -> "admin";
    }

    @Test
    void assignRoleToUser() {
        String email = "test@test.com";
        Long userId = 10L;

        User testUser = new User();
        testUser.setId(userId);
        Role testRole = new Role();
        testRole.setId(10L);
        testRole.setName("TEST_ROLE_NAME");

        when(userService.getUserByName("admin")).thenReturn(testUser);
        doNothing().when(userService).assignRoleToUser(email, testRole);
        doNothing().when(userActionLogService)
                .logAction(userId, "ROLE_" + testRole.getName() + " assigned to user " + email);

        ResponseEntity<String> response = adminController.assignRoleToUser(email, testRole, principal);

        assertEquals(ResponseEntity.ok("Role assigned to user"), response);
    }

    @Test
    void deleteUserSuccess() {
        long userIdToDelete = 10L;
        long adminId = 1L;

        User adminUser = new User();
        adminUser.setId(adminId);

        when(userService.getUserByName("admin")).thenReturn(adminUser);
        doNothing().when(userService).deleteUser(userIdToDelete);
        doNothing().when(userActionLogService)
                .logAction(adminId, "User " + userIdToDelete + " deleted from users");

        ResponseEntity<String> response = adminController.deleteUser(userIdToDelete, principal);

        assertEquals(ResponseEntity.ok("User deleted"), response);
        verify(userService).deleteUser(userIdToDelete);
        verify(userActionLogService).logAction(adminId, "User " + userIdToDelete + " deleted from users");
    }

    @Test
    void blockUser() {
        long userIdToBlock = 10L;
        long adminId = 1L;

        User adminUser = new User();
        adminUser.setId(adminId);

        when(userService.getUserByName("admin")).thenReturn(adminUser);
        doNothing().when(userService).banUser(userIdToBlock);
        doNothing().when(userActionLogService)
                .logAction(adminId, "User " + userIdToBlock + " blocked");

        ResponseEntity<String> response = adminController.blockUser(userIdToBlock, principal);

        assertEquals(ResponseEntity.ok("User blocked"), response);
        verify(userService).banUser(userIdToBlock);
        verify(userActionLogService).logAction(adminId, "User " + userIdToBlock + " blocked");
    }

    @Test
    void unblockUser() {
        long userIdToUnblock = 42L;
        long adminId = 1L;

        User adminUser = new User();
        adminUser.setId(adminId);

        when(userService.getUserByName("admin")).thenReturn(adminUser);
        doNothing().when(userService).unbanUser(userIdToUnblock);
        doNothing().when(userActionLogService)
                .logAction(adminId, "User " + userIdToUnblock + " unblocked");

        ResponseEntity<String> response = adminController.unblockUser(userIdToUnblock, principal);

        assertEquals(ResponseEntity.ok("User unblocked"), response);
        verify(userService).unbanUser(userIdToUnblock);
        verify(userActionLogService).logAction(adminId, "User " + userIdToUnblock + " unblocked");
    }

    @Test
    void getLogs() {
        long userIdToRequest = 42L;
        long adminId = 1L;

        User adminUser = new User();
        adminUser.setId(adminId);

        Map<LocalDateTime, String> logs = Map.of(
                LocalDateTime.of(2023, 11, 22, 10, 0), "User logged in",
                LocalDateTime.of(2023, 11, 22, 11, 0), "User updated profile"
        );

        User requestedUser = new User();
        requestedUser.setId(userIdToRequest);
        UserActionLog userActionLog = new UserActionLog();
        userActionLog.setActions(logs);
        requestedUser.setUserActionLog(userActionLog);

        when(userService.getUserByName("admin")).thenReturn(adminUser);
        when(userService.getUserById(userIdToRequest)).thenReturn(requestedUser);

        ArgumentCaptor<String> logMessageCaptor = ArgumentCaptor.forClass(String.class);
        doNothing().when(userActionLogService)
                .logAction(eq(adminId), logMessageCaptor.capture());

        Map<LocalDateTime, String> responseLogs = adminController.getLogs(userIdToRequest, principal);

        assertEquals(logs, responseLogs);
        verify(userService).getUserById(userIdToRequest);
        verify(userActionLogService).logAction(eq(adminId), anyString());

        String capturedLogMessage = logMessageCaptor.getValue();
        assertTrue(capturedLogMessage.contains("Reqested logs for User"));
        assertTrue(capturedLogMessage.contains(String.valueOf(userIdToRequest)));
    }

}
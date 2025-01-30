package habittracker.userservice.unit.test.controller;

import habittracker.userservice.controller.NonAutorizedController;
import habittracker.userservice.model.Role;
import habittracker.userservice.model.User;
import habittracker.userservice.service.PasswordResetService;
import habittracker.userservice.service.RoleService;
import habittracker.userservice.service.UserActionLogService;
import habittracker.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NonAutorizedControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private UserService userService;

    @Mock
    private UserActionLogService userActionLogService;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private NonAutorizedController nonAutorizedController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(nonAutorizedController).build();
    }

    @Test
    void testForgotPassword() throws Exception {
        String email = "test@example.com";

        mockMvc.perform(post("/auth/forgot-password")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().string("Forgot Password"));

        verify(passwordResetService, times(1)).forgotPassword(email);
    }

    @Test
    void testResetPassword() throws Exception {
        String token = "resetToken";
        String newPassword = "newPassword123";

        mockMvc.perform(post("/auth/reset-password")
                        .param("token", token)
                        .param("newPassword", newPassword))
                .andExpect(status().isOk())
                .andExpect(content().string("Reset Password"));

        verify(passwordResetService, times(1)).resetPassword(token, newPassword);
    }

    @Test
    void testNewUser() throws Exception {
        String userName = "testUser";
        String password = "password123";
        String email = "test@example.com";

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(userName);
        mockUser.setEmail(email);

        Role role = new Role();
        role.setName("USER");

        when(userService.getUserByName(userName)).thenReturn(mockUser);
        when(roleService.getRoleByName("USER")).thenReturn(role);

        mockMvc.perform(post("/auth/new-user")
                        .param("userName", userName)
                        .param("password", password)
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().string("New user added"));

        verify(userService, times(1)).addUser(userName, password, email);
        verify(userService, times(3)).getUserByName(userName);
        verify(roleService, times(1)).getRoleByName("USER");
        verify(userActionLogService, times(1)).logAction(1L, "User was created with id: 1");
        verify(userService, times(1)).assignRoleToUser(email, role);
        verify(userActionLogService, times(1)).logAction(1L, userName + "assigned role: USER");
    }
}

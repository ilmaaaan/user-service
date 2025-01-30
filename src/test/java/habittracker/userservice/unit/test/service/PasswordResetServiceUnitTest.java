package habittracker.userservice.unit.test.service;

import habittracker.userservice.model.PasswordResetToken;
import habittracker.userservice.model.User;
import habittracker.userservice.repository.PasswordResetTokenRepository;
import habittracker.userservice.repository.UserRepository;
import habittracker.userservice.service.impl.PasswordResetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PasswordResetServiceUnitTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("oldPassword");
    }

    @Test
    void forgotPasswordUser() {
        String email = "test@example.com";
        when(userRepository.findByEmailWithRoles(email)).thenReturn(Optional.of(user));

        passwordResetService.forgotPassword(email);

        verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(userRepository, never()).save(any()); // user repository should not be called yet
    }

    @Test
    void forgotPasswordUserNotFound() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmailWithRoles(email)).thenReturn(Optional.empty());

        RuntimeException exception
                = assertThrows(RuntimeException.class, () -> passwordResetService.forgotPassword(email));
        assertEquals("User with this email doesn't exist", exception.getMessage());
    }

    @Test
    void resetPasswordTokenExpired() {
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        resetToken.setExpiryDate(LocalDateTime.now().minusMinutes(1)); // Токен просрочен
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        passwordResetService.resetPassword(token, "newPassword");

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void resetPasswordTokenValid() {
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword");

        passwordResetService.resetPassword(token, "newPassword");

        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).save(user);
        assertEquals("encodedNewPassword", user.getPassword());
    }

    @Test
    void resetPasswordInvalidToken() {
        String invalidToken = UUID.randomUUID().toString();
        when(passwordResetTokenRepository.findByToken(invalidToken)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, ()
                -> passwordResetService.resetPassword(invalidToken, "newPassword"));
        assertEquals("Invalid token", exception.getMessage());
    }
}


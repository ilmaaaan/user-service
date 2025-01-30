package habittracker.userservice.service.impl;

import habittracker.userservice.model.PasswordResetToken;
import habittracker.userservice.model.User;
import habittracker.userservice.repository.PasswordResetTokenRepository;
import habittracker.userservice.repository.UserRepository;
import habittracker.userservice.service.PasswordResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetServiceImpl.class);

    @Autowired
    public PasswordResetServiceImpl(PasswordResetTokenRepository passwordResetTokenRepository,
                                    UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new RuntimeException("User with this email doesn't exist"));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(resetToken);

        String resetUrl = "http://localhost:8080/api/auth/reset-password?token=" + token;
        //логика отправки email.

        LOGGER.info("Password reset link sent to {}", email);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        // Проверка на истекший токен
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            LOGGER.warn("Attempt to reset password with expired token");
            return; // Если токен истек, ничего не делаем
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Логирование успешного сброса пароля
        LOGGER.info("Password reset successfully for user {}", user.getEmail());
    }
}

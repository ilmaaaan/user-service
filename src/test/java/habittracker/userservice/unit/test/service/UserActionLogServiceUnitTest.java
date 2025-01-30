package habittracker.userservice.unit.test.service;

import habittracker.userservice.audit.UserActionLog;
import habittracker.userservice.model.User;
import habittracker.userservice.repository.UserActionLogRepository;
import habittracker.userservice.repository.UserRepository;
import habittracker.userservice.service.impl.UserActionLogServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
class UserActionLogServiceUnitTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserActionLogRepository userActionLogRepository;

    @InjectMocks
    private UserActionLogServiceImpl userActionLogService;

    @Test
    void getUserActionLogFail() {
        when(userRepository.findByIdWithRoles(10L)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userActionLogService.logAction(10L, "testAction");
        });
    }

    @Test
    void getUserActionLogSuccess() {
        User user = new User();
        Map<LocalDateTime, String> actionLog = new HashMap<>();
        UserActionLog userActionLog = new UserActionLog();
        user.setId(10L);
        userActionLog.setActions(actionLog);
        user.setUserActionLog(userActionLog);

        when(userRepository.findByIdWithRoles(10L)).thenReturn(Optional.of(user));

        userActionLogService.logAction(10L, "testAction");

        // Убедимся, что действие было добавлено в actionLog с правильным временем
        assertTrue(actionLog.containsValue("testAction"));
    }
}

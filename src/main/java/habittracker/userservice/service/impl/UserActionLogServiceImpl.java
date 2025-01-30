package habittracker.userservice.service.impl;

import habittracker.userservice.model.User;
import habittracker.userservice.repository.UserActionLogRepository;
import habittracker.userservice.repository.UserRepository;
import habittracker.userservice.service.UserActionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserActionLogServiceImpl implements UserActionLogService {

    UserActionLogRepository userActionLogRepository;
    UserRepository userRepository;

    @Autowired
    public UserActionLogServiceImpl(UserActionLogRepository userActionLogRepository, UserRepository userRepository) {
        this.userActionLogRepository = userActionLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void logAction(Long id, String action) {
        User user = userRepository.findByIdWithRoles(id).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }
        Map<LocalDateTime, String> actionLog = user.getUserActionLog().getActions();
        actionLog.put(LocalDateTime.now(), action);
        user.getUserActionLog().setActions(actionLog);
        userActionLogRepository.save(user.getUserActionLog());
    }
}

package habittracker.userservice.service;

import org.springframework.stereotype.Service;

@Service
public interface UserActionLogService {
    void logAction(Long id, String action);
}
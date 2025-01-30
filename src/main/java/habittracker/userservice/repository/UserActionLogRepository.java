package habittracker.userservice.repository;

import habittracker.userservice.audit.UserActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActionLogRepository extends JpaRepository<UserActionLog, Long> {
}

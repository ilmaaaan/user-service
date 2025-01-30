package habittracker.userservice.actuator.repository;

import habittracker.userservice.actuator.entity.MainInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MainInfoRepository extends JpaRepository<MainInfo, Long> {
    @Query("select mi from MainInfo mi left join fetch mi.activeProfiles where mi.createdAt <= :timestamp")
    List<MainInfo> findAllWithCreatedAtBefore(@Param("timestamp") LocalDateTime timestamp);
}

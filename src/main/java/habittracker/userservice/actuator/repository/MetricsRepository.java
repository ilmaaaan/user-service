package habittracker.userservice.actuator.repository;

import habittracker.userservice.actuator.entity.Metrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MetricsRepository extends JpaRepository<Metrics, Long> {
    @Query("select m from Metrics m where m.createdAt <= :timestamp")
    List<Metrics> findAllWithCreatedAtBefore(@Param("timestamp") LocalDateTime timestamp);
}

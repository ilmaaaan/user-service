package habittracker.userservice.actuator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "metrics")
public class Metrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "uptime")
    private String uptime;
    @Column(name = "cpu_usage")
    private String cpuUsage;
    @Column(name = "memory_usage")
    private String memoryUsage;
    @Column(name = "build_version")
    private String buildVersion;
    @Column(name = "current_branch")
    private String currentBranch;
    @Column(name = "last_commit")
    private String lastCommit;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

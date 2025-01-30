package habittracker.userservice.actuator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "main_info")
public class MainInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "application_name")
    private String applicationName;
    @Column(name = "artifact_id")
    private String artifactId;
    @Column(name = "launch_time")
    private String launchTime;
    @Column(name = "app_version")
    private String appVersion;
    @Column(name = "context_path")
    private String contextPath;
    @ElementCollection
    @CollectionTable(name = "active_profiles", joinColumns = @JoinColumn(name = "main_info_id"))
    @Column(name = "active_profile")
    private List<String> activeProfiles;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
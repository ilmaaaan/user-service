package habittracker.userservice.audit;

import habittracker.userservice.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_action_logs")
public class UserActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
//    @PrimaryKeyJoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @ElementCollection
    @MapKeyColumn(name = "action_time")
    private Map<LocalDateTime, String> actions = new LinkedHashMap<>();
}
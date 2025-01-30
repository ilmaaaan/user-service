package habittracker.userservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "password-reset-token")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;  // Одноразовый токен

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;  // Пользователь, для которого сгенерирован токен

    @Column(nullable = false)
    private LocalDateTime expiryDate;  // Срок действия токена

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }

    // Конструктор, принимающий токен и пользователя, и устанавливающий дату истечения срока действия
    public PasswordResetToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusHours(24);  // Пример: токен действует 24 часа
    }
}

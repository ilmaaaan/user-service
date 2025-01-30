package habittracker.userservice.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class NotificationMessage implements Serializable {
    private String email;
    private String message;
}
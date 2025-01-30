package habittracker.userservice.actuator.dto;

import habittracker.userservice.actuator.entity.MainInfo;
import habittracker.userservice.actuator.entity.Metrics;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceInfoResponse {
    private MainInfo mainInfo;
    private Metrics metrics;
}

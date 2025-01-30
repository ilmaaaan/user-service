package habittracker.userservice.actuator.controller;

import habittracker.userservice.actuator.dto.ServiceInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ActuatorController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${actuator.url:default_value}")
    private String actuatorUrl;

    public ServiceInfoResponse getServiceInfo() {
        ResponseEntity<ServiceInfoResponse> response = restTemplate.getForEntity(this.actuatorUrl,
                ServiceInfoResponse.class);
        return response.getBody();
    }
}

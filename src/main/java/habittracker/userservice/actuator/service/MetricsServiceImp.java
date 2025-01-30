package habittracker.userservice.actuator.service;

import habittracker.userservice.actuator.controller.ActuatorController;
import habittracker.userservice.actuator.entity.Metrics;
import habittracker.userservice.actuator.repository.MetricsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class MetricsServiceImp implements ActuatorService<Metrics> {

    private final MetricsRepository metricsRepository;
    private final ActuatorController actuatorController;

    @Value("300")
    private Long seconds;

    public MetricsServiceImp(MetricsRepository metricsRepository,
                             ActuatorController actuatorController) {
        this.metricsRepository = metricsRepository;
        this.actuatorController = actuatorController;
    }

    @Override
    @Scheduled(fixedRateString = "180_000")
    public void saveInfo() {
        if (!Objects.isNull(actuatorController.getServiceInfo().getMetrics())) {
            metricsRepository.save(actuatorController.getServiceInfo().getMetrics());
        }
    }

    @Override
    public List<Metrics> getInfo(Long seconds) {
        LocalDateTime timestamp = LocalDateTime.now().minusSeconds(seconds);
        return metricsRepository.findAllWithCreatedAtBefore(timestamp);
    }

    @Override
    @Scheduled(initialDelayString = "300_000",
               fixedRateString = "300_000")
    public void deleteInfo() {
        List<Metrics> metricsList = getInfo(this.seconds);
        metricsRepository.deleteAll(metricsList);
    }
}

package habittracker.userservice.actuator.service;

import habittracker.userservice.actuator.controller.ActuatorController;
import habittracker.userservice.actuator.entity.MainInfo;
import habittracker.userservice.actuator.repository.MainInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class MainInfoServiceImp implements ActuatorService<MainInfo> {

    private final MainInfoRepository mainInfoRepository;
    private final ActuatorController actuatorController;

    @Value("${delete.before.seconds}")
    private Long seconds;

    public MainInfoServiceImp(MainInfoRepository mainInfoRepository,
                              ActuatorController actuatorController) {
        this.mainInfoRepository = mainInfoRepository;
        this.actuatorController = actuatorController;
    }

    @Override
    @Scheduled(fixedRateString = "${main.info.save.fixed.rate}")
    public void saveInfo() {
        if (!Objects.isNull(actuatorController.getServiceInfo().getMainInfo())) {
            mainInfoRepository.save(actuatorController.getServiceInfo().getMainInfo());
        }
    }

    @Override
    public List<MainInfo> getInfo(Long seconds) {
        LocalDateTime timestamp = LocalDateTime.now().minusSeconds(seconds);
        return mainInfoRepository.findAllWithCreatedAtBefore(timestamp);
    }

    @Override
    @Scheduled(initialDelayString = "${delete.initial.delay}",
            fixedRateString = "${delete.fixed.rate}")
    public void deleteInfo() {
        List<MainInfo> mainInfoList = getInfo(this.seconds);
        mainInfoRepository.deleteAll(mainInfoList);
    }
}

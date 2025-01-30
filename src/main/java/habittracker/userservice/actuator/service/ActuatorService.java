package habittracker.userservice.actuator.service;

import java.util.List;

public interface ActuatorService<T> {
    void saveInfo();

    List<T> getInfo(Long seconds);

    void deleteInfo();
}

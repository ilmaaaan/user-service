package habittracker.userservice.service.impl;

import habittracker.userservice.client.UserFeignClient;
import habittracker.userservice.dto.habit.HabitDto;
import habittracker.userservice.dto.habit.HabitEntityProjectionDto;
import habittracker.userservice.dto.habit.request.HabitFilterRequestDto;
import habittracker.userservice.mapper.UserHabitMapper;
import habittracker.userservice.service.HabitIntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class HabitIntegrationServiceImpl implements HabitIntegrationService {

    private final UserFeignClient userFeignClient;

    private final UserHabitMapper mapper;

    public HabitIntegrationServiceImpl(UserFeignClient userFeignClient, UserHabitMapper mapper) {
        this.userFeignClient = userFeignClient;
        this.mapper = mapper;
    }

    @Override
    public HabitEntityProjectionDto createHabit(HabitDto habitDto) {
        ResponseEntity<HabitEntityProjectionDto> habit = userFeignClient.createHabit(habitDto);
        return habit.getBody();
    }

    @Override
    public Double getHabitProgress(Long habitId) {
        ResponseEntity<Double> habitProgress = userFeignClient.getHabitProgress(habitId);
        return habitProgress.getBody();
    }

    @Override
    public HabitDto updateHabitProgress(Long habitId) {
        ResponseEntity<HabitDto> habitDtoResponseEntity = userFeignClient.updateHabitProgress(habitId);
        return habitDtoResponseEntity.getBody();
    }

    @Override
    public Boolean checkReminder(Long habitId) {
        return userFeignClient.checkReminder(habitId).getBody();
    }

    @Override
    public HabitDto getHabitById(Long id) {
        ResponseEntity<HabitDto> habitDtoResponseEntity = userFeignClient.getHabitById(id);
        return habitDtoResponseEntity.getBody();
    }

    @Override
    public List<HabitDto> getAllHabits() {
        ResponseEntity<List<HabitDto>> allHabits = userFeignClient.getAllHabits();
        return allHabits.getBody();
    }

    @Override
    public HabitDto updateHabit(Long id, HabitDto habitDto) {
        ResponseEntity<HabitDto> habitDtoResponseEntity = userFeignClient.updateHabit(id, habitDto);
        return habitDtoResponseEntity.getBody();
    }

    @Override
    public Void deleteHabits(Long id) {
        userFeignClient.deleteHabits(id);
        return null;
    }

    @Override
    public Page<HabitEntityProjectionDto> getFilterHabits(HabitFilterRequestDto habitFilterRequestDto) {
        ResponseEntity<Page<HabitEntityProjectionDto>> filterHabits =
                userFeignClient.getFilterHabits(habitFilterRequestDto);
        return filterHabits.getBody();
    }
}

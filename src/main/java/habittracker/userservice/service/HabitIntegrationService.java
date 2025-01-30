package habittracker.userservice.service;

import habittracker.userservice.dto.habit.HabitDto;
import habittracker.userservice.dto.habit.HabitEntityProjectionDto;
import habittracker.userservice.dto.habit.request.HabitFilterRequestDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HabitIntegrationService {

    HabitEntityProjectionDto createHabit(HabitDto habitDto);

    Double getHabitProgress(Long habitId);

    HabitDto updateHabitProgress(Long habitId);

    Boolean checkReminder(Long habitId);

    HabitDto getHabitById(Long id);

    List<HabitDto> getAllHabits();

    HabitDto updateHabit(Long id, HabitDto habitDto);

    Void deleteHabits(Long id);

    Page<HabitEntityProjectionDto> getFilterHabits(HabitFilterRequestDto habitFilterRequestDto);
}

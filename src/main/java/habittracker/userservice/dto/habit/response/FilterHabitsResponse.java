package habittracker.userservice.dto.habit.response;

import habittracker.userservice.dto.habit.HabitDto;

import java.util.List;

public class FilterHabitsResponse {

    private List<HabitDto> habits;
    private HabitResponseStatus status;

    // Геттеры и сеттеры
    public List<HabitDto> getHabits() {
        return habits;
    }

    public void setHabits(List<HabitDto> habits) {
        this.habits = habits;
    }

    public HabitResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HabitResponseStatus status) {
        this.status = status;
    }

}

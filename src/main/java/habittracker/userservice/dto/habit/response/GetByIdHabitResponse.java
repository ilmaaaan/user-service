package habittracker.userservice.dto.habit.response;

import habittracker.userservice.dto.habit.HabitDto;

public class GetByIdHabitResponse {

    private HabitDto habit;
    private HabitResponseStatus status;

    public HabitDto getHabit() {
        return habit;
    }

    public void setHabit(HabitDto habit) {
        this.habit = habit;
    }

    public HabitResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HabitResponseStatus status) {
        this.status = status;
    }
}

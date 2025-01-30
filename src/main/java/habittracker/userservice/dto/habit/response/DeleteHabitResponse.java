package habittracker.userservice.dto.habit.response;

public class DeleteHabitResponse {

    private Long habitId;
    private HabitResponseStatus status;

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }

    public HabitResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HabitResponseStatus status) {
        this.status = status;
    }
}

package habittracker.userservice.dto.habit.response;

public class CreationHabitResponse {

    private String habitName;
    private HabitResponseStatus status;

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public HabitResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HabitResponseStatus status) {
        this.status = status;
    }
}

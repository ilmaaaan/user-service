package habittracker.userservice.dto.habit.response;

import lombok.Data;

@Data
public class UpdateFullHabitResponse {

    private String habitName;
    private String description;
    private HabitResponseStatus status;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HabitResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HabitResponseStatus status) {
        this.status = status;
    }

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }
}

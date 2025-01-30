package habittracker.userservice.dto.habit.response;

import lombok.Data;

@Data
public class UpdatedHabitResponse {

    private String habitName;
    private Double progress;
    private HabitResponseStatus status;

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public HabitResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HabitResponseStatus status) {
        this.status = status;
    }
}

package habittracker.userservice.dto.habit.response;

//@Data
public class ProgressionHabitResponse {

    private Double progress;

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

    private HabitResponseStatus status;
}

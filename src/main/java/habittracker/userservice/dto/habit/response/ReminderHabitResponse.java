package habittracker.userservice.dto.habit.response;

public class ReminderHabitResponse {

    private Boolean needsReminder;
    private HabitResponseStatus status;

    public Boolean getNeedsReminder() {
        return needsReminder;
    }

    public void setNeedsReminder(Boolean needsReminder) {
        this.needsReminder = needsReminder;
    }

    public HabitResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HabitResponseStatus status) {
        this.status = status;
    }
}

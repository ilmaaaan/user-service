package habittracker.userservice.dto.habit.response;

import lombok.Data;

@Data
public class GetAllHabitsResponse {

    private Long id;
    private String name;
    private String description;
    private double progress;
    private HabitResponseStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HabitResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HabitResponseStatus status) {
        this.status = status;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
